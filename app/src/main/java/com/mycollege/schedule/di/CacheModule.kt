package com.mycollege.schedule.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mycollege.schedule.data.cache.CacheManager
import com.mycollege.schedule.domain.background.CacheUpdater
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideCacheManager(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideCacheUpdater(cacheManager: CacheManager): CacheUpdater {
        return CacheUpdater(cacheManager)
    }

}