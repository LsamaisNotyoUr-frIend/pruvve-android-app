package com.fluture.pruvve.auth


import android.content.Context
import android.content.SharedPreferences

object TeamManager {
    private const val PREF_NAME = "team_pref"
    private const val KEY_TEAM_ID = "team_id"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(id: Int) {
        sharedPreferences?.edit()?.putInt(KEY_TEAM_ID, id)?.apply()
    }

    fun getTeamId(): Int {
        return TeamManager.sharedPreferences?.getInt(KEY_TEAM_ID, 2) ?: 9
    }

    fun clearToken() {
        sharedPreferences?.edit()?.remove(KEY_TEAM_ID)?.apply()
    }
}