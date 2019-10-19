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

    private val _exchangeRates = MutableLiveData<List<ExchangeRate>>()
    val exchangeRates: LiveData<List<ExchangeRate>>
        get() = _exchangeRates

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

    fun onItemClick(exchangeRate: ExchangeRate) {
        compositeDisposable.clear()
        observeExchangeRates(exchangeRate.value, exchangeRate.currencyCode)
    }

    fun onValueChanged(exchangeRate: ExchangeRate, items: List<ExchangeRate>) {
        compositeDisposable.clear()
        updateValuesImmediately(exchangeRate, items)
        observeExchangeRates(exchangeRate.value, exchangeRate.currencyCode)
    }

    private fun updateValuesImmediately(exchangeRate: ExchangeRate, items: List<ExchangeRate>) {
        _exchangeRates.value = items.map {
            val newValue = it.rate * exchangeRate.value
            it.copy(value = newValue.round(2))
        }
    }

    private fun observeExchangeRates(value: Double, base: String) {
        compositeDisposable + exchangeRateRepository.observeExchangeRates(base)
            .subscribeOn(schedulerConfiguration.computation())
            .observeOn(schedulerConfiguration.ui())
            .subscribe({ response ->
                _exchangeRates.value = getExchangeRatesWithBase(value, base, response)
                _scrollToTop.value = base
            }, { error ->
                Timber.e(
                    error,
                    "getExchangeRates(value=$value, base=$base, scrollToTop=$scrollToTop)"
                )
                _showError.call()
            })
    }

    private fun getExchangeRatesWithBase(
        value: Double,
        base: String,
        response: ExchangeRateResponse
    ): List<ExchangeRate> {
        val baseRate = getBaseExchangeRate(value, base)
        val list = mutableListOf(baseRate)
        list.addAll(getExchangeRateList(value, response))
        return list
    }

    private fun getBaseExchangeRate(value: Double, base: String) = ExchangeRate(
        currencyName = currencyNameProvider.getDisplayName(base),
        currencyCode = base,
        rate = 1.0,
        value = value.round(2),
        editable = true
    )

    private fun getExchangeRateList(
        value: Double,
        response: ExchangeRateResponse
    ) = response.rates.map {
        ExchangeRate(
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