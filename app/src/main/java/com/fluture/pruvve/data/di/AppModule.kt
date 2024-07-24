package com.fluture.pruvve.data.di

import android.content.Context
import android.content.SharedPreferences
import com.fluture.pruvve.data.repository.SignUpRepository
import com.fluture.pruvve.data.api.UserService
import com.fluture.pruvve.data.repository.SignUpRepositoryImpl
import com.fluture.pruvve.data.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("auth_pref", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(userService: UserService): SignUpRepository =
        SignUpRepositoryImpl(userService)
}