package dev.siegmund.exchange.api.model

import com.squareup.moshi.Json
import java.util.*

data class ExchangeRateResponse(
    @field:Json(name = "base") val base: String,
    @field:Json(name = "date") val date: Date,
    @field:Json(name = "rates") val rates: Map<String, Double>
)