package dev.siegmund.exchange.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dev.siegmund.core.api.ApiInterceptor
import dev.siegmund.core.rx.SchedulerConfiguration
import dev.siegmund.exchange.api.ExchangeRateApi
import dev.siegmund.exchange.api.ExchangeRateRepository
import dev.siegmund.exchange.api.ExchangeRateRepositoryImpl
import dev.siegmund.exchange.ui.model.ExchangeRateViewModelFactory
import dev.siegmund.exchange.ui.util.CurrencyNameProvider
import dev.siegmund.exchange.ui.util.CurrencyNameProviderImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class ExchangeModule {
    @Provides
    fun provideExchangeRateApi(moshi: Moshi): ExchangeRateApi {
        val apiClient = OkHttpClient.Builder().addInterceptor(ApiInterceptor()).build()
        return Retrofit.Builder().apply {
            baseUrl("https://revolut.duckdns.org/")
            addConverterFactory(MoshiConverterFactory.create(moshi))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(apiClient)
        }.build().create(ExchangeRateApi::class.java)
    }

    @Provides
    fun provideExchangeRateRepository(
        exchangeRateApi: ExchangeRateApi
    ): ExchangeRateRepository = ExchangeRateRepositoryImpl(exchangeRateApi)

    @Provides
    fun provideCurrencyNameProvider(): CurrencyNameProvider = CurrencyNameProviderImpl()

    @Provides
    fun provideExchangeRateViewModelFactory(
        exchangeRateRepository: ExchangeRateRepository,
        schedulerConfiguration: SchedulerConfiguration,
        currencyNameProvider: CurrencyNameProvider
    ): ExchangeRateViewModelFactory =
        ExchangeRateViewModelFactory(
            exchangeRateRepository,
            schedulerConfiguration,
            currencyNameProvider
        )
}