package dev.siegmund.exchangeratecalculator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.siegmund.exchangeratecalculator.App
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun providesAppContext(application: App): Context {
        return application.applicationContext
    }
}