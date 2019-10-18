package dev.siegmund.exchange.ui.util

interface CurrencyNameProvider {
    fun getDisplayName(currencyCode: String): String?
}