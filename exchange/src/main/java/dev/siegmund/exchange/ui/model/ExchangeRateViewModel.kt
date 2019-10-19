package dev.siegmund.exchange.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dev.siegmund.core.rx.SchedulerConfiguration
import dev.siegmund.core.rx.extensions.plus
import dev.siegmund.core.ui.SingleLiveEvent
import dev.siegmund.exchange.api.ExchangeRateRepository
import dev.siegmund.exchange.api.model.ExchangeRateResponse
import dev.siegmund.exchange.ui.util.CurrencyNameProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import kotlin.math.round

class ExchangeRateViewModel(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val schedulerConfiguration: SchedulerConfiguration,
    private val currencyNameProvider: CurrencyNameProvider
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _exchangeRateItems = MutableLiveData<List<ExchangeRateItem>>()
    val exchangeRateItems: LiveData<List<ExchangeRateItem>>
        get() = _exchangeRateItems

    private val _showError = SingleLiveEvent<Unit>()
    val showError: LiveData<Unit>
        get() = _showError

    private val _scrollToTop = MutableLiveData<String>()
    val scrollToTop: LiveData<String>
        get() = Transformations.distinctUntilChanged(_scrollToTop)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onCreate() {
        compositeDisposable.clear()
        observeExchangeRates(DEFAULT_VALUE, DEFAULT_CURRENCY_CODE)
    }

    fun onItemClick(exchangeRateItem: ExchangeRateItem) {
        compositeDisposable.clear()
        observeExchangeRates(exchangeRateItem.value, exchangeRateItem.currencyCode)
    }

    fun onValueChanged(exchangeRateItem: ExchangeRateItem, items: List<ExchangeRateItem>) {
        compositeDisposable.clear()
        updateValuesImmediately(exchangeRateItem, items)
        observeExchangeRates(exchangeRateItem.value, exchangeRateItem.currencyCode)
    }

    private fun updateValuesImmediately(
        exchangeRateItem: ExchangeRateItem,
        items: List<ExchangeRateItem>
    ) {
        _exchangeRateItems.value = items.map {
            val newValue = it.rate * exchangeRateItem.value
            it.copy(value = newValue.round(2))
        }
    }

    private fun observeExchangeRates(value: Double, base: String) {
        compositeDisposable + exchangeRateRepository.observeExchangeRates(base)
            .subscribeOn(schedulerConfiguration.computation())
            .observeOn(schedulerConfiguration.ui())
            .subscribe({ response ->
                _exchangeRateItems.value = getExchangeRateItemsWithBase(value, base, response)
                _scrollToTop.value = base
            }, { error ->
                Timber.e(
                    error,
                    "observeExchangeRates(value=$value, base=$base)"
                )
                _showError.call()
            })
    }

    private fun getExchangeRateItemsWithBase(
        value: Double,
        base: String,
        response: ExchangeRateResponse
    ): List<ExchangeRateItem> {
        val baseRate = getBaseExchangeRateItem(value, base)
        val list = mutableListOf(baseRate)
        list.addAll(getExchangeRateItemList(value, response))
        return list
    }

    private fun getBaseExchangeRateItem(value: Double, base: String) = ExchangeRateItem(
        currencyName = currencyNameProvider.getDisplayName(base),
        currencyCode = base,
        rate = 1.0,
        value = value.round(2),
        editable = true
    )

    private fun getExchangeRateItemList(
        value: Double,
        response: ExchangeRateResponse
    ) = response.rates.map {
        ExchangeRateItem(
            currencyName = currencyNameProvider.getDisplayName(it.key),
            currencyCode = it.key,
            rate = it.value,
            value = (it.value * value).round(2)
        )
    }

    private companion object {
        const val DEFAULT_VALUE = 1.0
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}