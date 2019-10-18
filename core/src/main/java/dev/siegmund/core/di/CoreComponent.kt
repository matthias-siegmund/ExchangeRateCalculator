package dev.siegmund.core.di

import com.squareup.moshi.Moshi
import dagger.Component
import dev.siegmund.core.rx.SchedulerConfiguration
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {
    fun provideMoshi(): Moshi

    fun provideSchedulerConfiguration(): SchedulerConfiguration
}