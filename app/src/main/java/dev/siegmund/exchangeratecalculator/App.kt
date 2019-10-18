package dev.siegmund.exchangeratecalculator

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.siegmund.core.di.CoreComponent
import dev.siegmund.core.di.CoreComponentProvider
import dev.siegmund.core.di.DaggerCoreComponent
import dev.siegmund.exchangeratecalculator.di.DaggerAppComponent
import timber.log.Timber

class App : DaggerApplication(), CoreComponentProvider {

    override val coreComponent: CoreComponent by lazy {
        DaggerCoreComponent
            .builder()
            .build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent
            .builder()
            .application(this)
            .coreComponent(coreComponent)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}