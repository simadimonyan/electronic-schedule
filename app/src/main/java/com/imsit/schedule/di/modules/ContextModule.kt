package com.imsit.schedule.di.modules

import android.content.Context
import com.imsit.schedule.di.ResourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {

    @Provides
    fun provideResourceManager(@ApplicationContext context: Context): ResourceManager {
        return ResourceManager(context)
    }

}