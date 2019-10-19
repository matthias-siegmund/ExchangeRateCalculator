package dev.siegmund.exchange.ui.util

import java.util.*

class CurrencyNameProviderImpl : CurrencyNameProvider {
    override fun getDisplayName(currencyCode: String): String? =
        Currency.getInstance(currencyCode).displayName
}