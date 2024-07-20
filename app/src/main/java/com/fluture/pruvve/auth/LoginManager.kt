package com.fluture.pruvve.auth

import android.content.Context
import android.content.SharedPreferences

object LoginManager {
    private const val PREF_NAME = "auth_pref"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_ACCOUNT_TYPE = "account_type"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_PROFILE_URL = "profile_url"


    private var sharedPreferences: SharedPreferences? = null


    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }
    fun saveProfileUrl(profileUrl: String) {
        sharedPreferences?.edit()?.putString(KEY_PROFILE_URL, profileUrl)?.apply()
    }
    fun saveAccountType(accountType: String) {
        sharedPreferences?.edit()?.putString(KEY_ACCOUNT_TYPE, accountType)?.apply()
    }

    fun saveUserInfo(userId: Int, username: String, accountType: String) {
        sharedPreferences?.edit()?.apply {
            putString(KEY_TOKEN, getToken())
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }?.apply()
    }

    fun getToken(): String? = sharedPreferences?.getString(KEY_TOKEN, null)
    fun getUserId(): Int = sharedPreferences?.getInt(KEY_USER_ID, -1) ?: -1
    fun getUsername(): String? = sharedPreferences?.getString(KEY_USERNAME, null)
    fun getAccountType(): String? = sharedPreferences?.getString(KEY_ACCOUNT_TYPE, null)
    fun isLoggedIn(): Boolean = sharedPreferences?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    fun getProfileUrl(): String? = sharedPreferences?.getString(KEY_PROFILE_URL, "https://i.pinimg.com/564x/63/9c/7b/639c7be5f3ebe958d761cb2c614884dc.jpg")

    fun clearUserInfo() {
        sharedPreferences?.edit()?.clear()?.apply()
    }
}