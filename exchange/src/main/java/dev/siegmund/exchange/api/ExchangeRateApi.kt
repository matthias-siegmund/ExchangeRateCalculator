package dev.siegmund.exchange.api

import dev.siegmund.exchange.api.model.ExchangeRateResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET(EXCHANGE_RATE)
    fun getExchangeRates(@Query("base") base: String): Single<ExchangeRateResponse>

    private companion object {
        const val EXCHANGE_RATE = "latest"
    }
}