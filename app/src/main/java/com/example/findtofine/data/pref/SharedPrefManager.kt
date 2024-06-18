package com.example.findtofine.data.pref

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {
    private const val PREF_NAME = "user_pref"
    private const val UID = "uid"
    private const val EMAIL = "email"
    const val TOKEN = "token"
    private const val FIRST_NAME = "first_name"
    private const val LAST_NAME = "last_name"
    private const val DATE_OF_BIRTH = "date_of_birth"
    private const val PROFILE_PICTURE = "profile_picture"

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

    fun saveProfileData(context: Context, firstName: String, lastName: String, dateOfBirth: String, profilePicture: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(FIRST_NAME, firstName)
        editor.putString(LAST_NAME, lastName)
        editor.putString(DATE_OF_BIRTH, dateOfBirth)
        editor.putString(PROFILE_PICTURE, profilePicture)
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
            TOKEN to sharedPreferences.getString(TOKEN, null),
            FIRST_NAME to sharedPreferences.getString(FIRST_NAME, null),
            LAST_NAME to sharedPreferences.getString(LAST_NAME, null),
            DATE_OF_BIRTH to sharedPreferences.getString(DATE_OF_BIRTH, null),
            PROFILE_PICTURE to sharedPreferences.getString(PROFILE_PICTURE, null)
        )
    }
}