package com.matijasokol.dotainfo.di

import android.app.Application
import coil.ImageLoader
import coil.memory.MemoryCache
import com.matijasokol.dotainfo.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        app: Application
    ): ImageLoader {
        return ImageLoader.Builder(app)
            .error(R.drawable.error_image)
            .placeholder(R.drawable.white_background)
            .memoryCache(MemoryCache.Builder(app).maxSizePercent(0.25).build())
            .crossfade(true)
            .build()
    }
}