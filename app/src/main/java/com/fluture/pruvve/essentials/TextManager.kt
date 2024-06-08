package com.fluture.pruvve.essentials

import android.content.Context
import android.content.SharedPreferences

object TextManager {
    private const val PREF_NAME = "text_pref"
    private const val TEXT = "gotten_text"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveText(text: String) {
        sharedPreferences?.edit()?.putString(TEXT, text)?.apply()
    }

    fun getText(): String? {
        return sharedPreferences?.getString(TEXT, null)
    }

    fun clearText() {
        sharedPreferences?.edit()?.remove(TEXT)?.apply()
    }
}