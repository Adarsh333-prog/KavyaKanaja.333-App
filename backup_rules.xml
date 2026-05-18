package com.app.kavyakanaja.util

import android.content.Context
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

fun initCoil(context: Context) {
    val imageLoader = ImageLoader.Builder(context)
        .okHttpClient {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "KavyaKanajaApp/1.0 (Android)")
                        .build()
                    chain.proceed(request)
                }
                .build()
        }
        .build()
    Coil.setImageLoader(imageLoader)
}