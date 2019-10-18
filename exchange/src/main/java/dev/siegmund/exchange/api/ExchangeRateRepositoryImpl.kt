package dev.siegmund.exchange.api

import dev.siegmund.exchange.api.model.ExchangeRateResponse
import io.reactivex.Single

class ExchangeRateRepositoryImpl(
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {
    override fun getExchangeRates(base: String): Single<ExchangeRateResponse> {
        return exchangeRateApi.getExchangeRates(base)
    }
}