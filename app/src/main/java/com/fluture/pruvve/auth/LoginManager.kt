package com.fluture.pruvve.auth

import android.content.Context
import android.content.SharedPreferences

object LoginManager {
    private const val PREF_NAME = "auth_pref"
    private const val KEY_TOKEN = "auth_token"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return sharedPreferences?.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}