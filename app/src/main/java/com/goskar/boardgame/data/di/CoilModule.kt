package com.goskar.boardgame.data.di

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.coilModule() = module {
    single<ImageLoaderFactory> {
        ImageLoaderFactoryImpl(get())
    }
}

class ImageLoaderFactoryImpl(private val context: Context) : ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader
            .Builder(context)
            .logger(DebugLogger())
            .memoryCache(
                MemoryCache
                    .Builder(context)
                    .maxSizePercent(0.10)
                    .build()
            )
            .diskCache(
                DiskCache
                    .Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            )
            .components {
                add(SvgDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}