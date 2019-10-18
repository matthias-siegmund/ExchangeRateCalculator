package dev.siegmund.exchange.api

import dev.siegmund.exchange.api.model.ExchangeRateResponse
import io.reactivex.Single

interface ExchangeRateRepository {
    fun getExchangeRates(base: String): Single<ExchangeRateResponse>
}