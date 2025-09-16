package com.reicode.stopgame.di

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
        return SignalRService("http://10.0.2.2:5077/gameHub")
    }
}
