package com.example.findtofine.data.pref

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {
    private const val PREF_NAME = "user_pref"
    private const val UID = "uid"
    private const val EMAIL = "email"
    const val TOKEN = "token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserData(context: Context, uid: String, email: String, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(UID, uid)
        editor.putString(EMAIL, email)
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(TOKEN, null) != null
    }

    fun clearUserData(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

    fun getUserData(context: Context): Map<String, String?> {
        val sharedPreferences = getSharedPreferences(context)
        return mapOf(
            UID to sharedPreferences.getString(UID, null),
            EMAIL to sharedPreferences.getString(EMAIL, null),
            TOKEN to sharedPreferences.getString(TOKEN, null)
        )
    }
}