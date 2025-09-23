package com.reicode.stopgame.di

import com.reicode.stopgame.BuildConfig
import com.reicode.stopgame.realtime.SignalRService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignalRModule {

    @Provides
    @Singleton
    fun provideSignalRService(): SignalRService {
        return SignalRService(BuildConfig.SIGNALR_HUB_URL)
    }
}
