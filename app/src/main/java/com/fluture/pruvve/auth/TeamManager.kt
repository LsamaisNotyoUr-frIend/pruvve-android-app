package com.fluture.pruvve.auth


import android.content.Context
import android.content.SharedPreferences

object TeamManager {
    private const val PREF_NAME = "team_pref"
    private const val TEAM_ID = "team_token"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(id: Int) {
        sharedPreferences?.edit()?.putInt(TEAM_ID, id)?.apply()
    }

    fun getToken(): Int? {
        return sharedPreferences?.getInt(TEAM_ID, 1)
    }

    fun clearToken() {
        sharedPreferences?.edit()?.remove(TEAM_ID)?.apply()
    }
}