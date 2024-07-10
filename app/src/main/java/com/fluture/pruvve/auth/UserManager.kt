package com.fluture.pruvve.auth

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREF_NAME = "auth_pref"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_PROFILE_URL = "profile_url"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserCredentials(userId: Int, username: String, profileUrl: String?) {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_PROFILE_URL, profileUrl)
        }?.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences?.getInt(KEY_USER_ID, -1) ?: -1
    }

    fun getUsername(): String? {
        return sharedPreferences?.getString(KEY_USERNAME, null)
    }

    fun getProfileUrl(): String? {
        return sharedPreferences?.getString(KEY_PROFILE_URL, null)
    }

    fun clearUserCredentials() {
        sharedPreferences?.edit()?.apply {
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_PROFILE_URL)
        }?.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return getUserId() != -1 && !getUsername().isNullOrEmpty()
    }
}