package dev.siegmund.core.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dev.siegmund.core.rx.SchedulerConfiguration
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Singleton

@Module
class CoreModule {
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideSchedulerConfiguration(): SchedulerConfiguration = object : SchedulerConfiguration {
        override fun io(): Scheduler = Schedulers.io()

        override fun computation(): Scheduler = Schedulers.computation()

        override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    }
}