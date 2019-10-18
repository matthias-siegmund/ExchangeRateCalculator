package dev.siegmund.exchange.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.siegmund.core.rx.SchedulerConfiguration
import dev.siegmund.exchange.api.ExchangeRateRepository
import dev.siegmund.exchange.ui.util.CurrencyNameProvider

class ExchangeRateViewModelFactory(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val schedulerConfiguration: SchedulerConfiguration,
    private val currencyNameProvider: CurrencyNameProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExchangeRateViewModel::class.java)) {
            return ExchangeRateViewModel(
                exchangeRateRepository,
                schedulerConfiguration,
                currencyNameProvider
            ) as T
        }
        throw IllegalArgumentException()
    }
}