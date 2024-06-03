package com.fluture.pruvve.essentials

import android.content.Context
import android.util.LruCache
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.retrofittcalls.GetAllPosts
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

// Function to create an OkHttpClient with caching and authentication
fun provideOkHttpClient(context: Context, token: String): OkHttpClient {
    val hardCapCacheSize = 800 * 1024 * 1024 // 800 MB
    val softCapCacheSize = 200 * 1024 * 1024 // 200 MB
    val cacheDir = File(context.cacheDir, "http_cache")
    val cache = Cache(cacheDir, hardCapCacheSize.toLong())

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(AuthInterceptor(token))
        .addInterceptor(logging)
        .addNetworkInterceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = response.header("Cache-Control")
            if (cacheControl == null) {
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60) // 1 minute cache
                    .build()
            } else {
                response
            }
        }
        .addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            // Custom logic to check and manage cache size
            manageCacheSize(context, cacheDir, softCapCacheSize)
            response
        }
        .build()
}
fun manageCacheSize(context: Context, cacheDir: File, softCapCacheSize: Int) {
    val cache = Cache(cacheDir, softCapCacheSize.toLong())
    if (cache.size() > softCapCacheSize) {
        cache.evictAll() // Evict all cache entries. Customize as needed.
    }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}

object MemoryCache {
    private var maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val cache = object : LruCache<String, GetAllPosts>(cacheSize) {
        override fun sizeOf(key: String, value: GetAllPosts): Int {
            // Measure item size in kilobytes (e.g., assuming each item is 1KB)
            return 1  // Adjust this to properly calculate the size
        }
    }

    fun put(key: String, data: GetAllPosts) {
        cache.put(key, data)
    }

    fun get(key: String): GetAllPosts? {
        return cache.get(key)
    }
}