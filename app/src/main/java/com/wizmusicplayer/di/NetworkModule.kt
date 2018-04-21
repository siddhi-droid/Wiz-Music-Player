package com.wizmusicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.wizmusicplayer.BuildConfig
import com.wizmusicplayer.Config
import com.wizmusicplayer.networking.APIService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module(includes = [(ApplicationContextModule::class)])
class NetworkModule {

    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Config.BING_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun getAPIService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun getCacheFile(context: Context): File {
        return File(context.cacheDir, "Wiz_Cache")
    }

    @Provides
    @Singleton
    fun getCache(cacheFile: File): Cache {
        return Cache(cacheFile, 10 * 1000 * 1000)
    }

    @Provides
    @Singleton
    fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    @Provides
    @Singleton
    fun getOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, cache: Cache, sharedPreferences: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
//                .addInterceptor { chain ->
//                    val request = chain.request().newBuilder()
//                            .addHeader("Access-Token", sharedPreferences.getString(SharedPrefsKeys.ACCESS_TOKEN, ""))
//                            .addHeader("Client", sharedPreferences.getString(SharedPrefsKeys.CLIENT, ""))
//                            .addHeader("Uid", sharedPreferences.getString(SharedPrefsKeys.UID, ""))
//                            .build()
//                    chain.proceed(request)
//                }
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    fun getGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

}