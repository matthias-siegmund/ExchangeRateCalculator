package dev.siegmund.exchange.api

import dev.siegmund.exchange.api.model.ExchangeRateResponse
import io.reactivex.Observable
import io.reactivex.Single

interface ExchangeRateRepository {
    fun getExchangeRates(base: String): Single<ExchangeRateResponse>

    fun observeExchangeRates(base: String): Observable<ExchangeRateResponse>
}