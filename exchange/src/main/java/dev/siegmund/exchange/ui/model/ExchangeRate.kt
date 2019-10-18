package dev.siegmund.exchange.ui.model

data class ExchangeRate(
    val currencyName: String?,
    val currencyCode: String,
    val value: Double
)