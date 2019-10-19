package dev.siegmund.exchange.api

import dev.siegmund.core.rx.SchedulerConfiguration
import dev.siegmund.exchange.api.model.ExchangeRateResponse
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class ExchangeRateRepositoryImpl(
    private val exchangeRateApi: ExchangeRateApi,
    private val schedulerConfiguration: SchedulerConfiguration
) : ExchangeRateRepository {
    override fun getExchangeRates(base: String): Single<ExchangeRateResponse> {
        return exchangeRateApi.getExchangeRates(base)
    }

    override fun observeExchangeRates(base: String): Observable<ExchangeRateResponse> {
        return Observable.interval(
            REFRESH_INTERVAL,
            TimeUnit.SECONDS,
            schedulerConfiguration.timer()
        ).switchMapSingle { exchangeRateApi.getExchangeRates(base) }
    }

    private companion object {
        const val REFRESH_INTERVAL = 1L
    }
}