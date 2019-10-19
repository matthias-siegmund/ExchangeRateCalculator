package dev.siegmund.exchange.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.siegmund.core.rx.SchedulerConfiguration
import dev.siegmund.core.rx.extensions.plus
import dev.siegmund.core.ui.SingleLiveEvent
import dev.siegmund.exchange.api.ExchangeRateRepository
import dev.siegmund.exchange.api.model.ExchangeRateResponse
import dev.siegmund.exchange.ui.util.CurrencyNameProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

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

    fun onStart() {
        observeExchangeRates("EUR")
    }

    private fun observeExchangeRates(base: String) {
        compositeDisposable + exchangeRateRepository.observeExchangeRates(base)
            .subscribeOn(schedulerConfiguration.computation())
            .observeOn(schedulerConfiguration.ui())
            .subscribe({ response ->
                Timber.v("observeExchangeRates(base=$base)")
                _exchangeRates.value = getExchangeRateList(response)
            }, { error ->
                Timber.e(error, "getExchangeRates()")
                _showError.call()
            })
    }

    private fun getExchangeRateList(response: ExchangeRateResponse) = response.rates.map {
        ExchangeRate(
            currencyName = currencyNameProvider.getDisplayName(it.key),
            currencyCode = it.key,
            value = it.value
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}