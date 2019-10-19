package dev.siegmund.exchange.ui.model

data class ExchangeRate(
    val currencyName: String?,
    val currencyCode: String,
    val value: Double,
    val rate: Double,
    val editable: Boolean = false,
    val clickable: Boolean = true
)