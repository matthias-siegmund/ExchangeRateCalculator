package dev.siegmund.exchangeratecalculator.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.siegmund.exchange.di.ExchangeModule
import dev.siegmund.exchange.ui.ExchangeRateFragment

@Module
abstract class BindingModule {
    @Suppress("unused")
    @ContributesAndroidInjector(modules = [ExchangeModule::class])
    internal abstract fun bindExchangeRateFragment(): ExchangeRateFragment
}