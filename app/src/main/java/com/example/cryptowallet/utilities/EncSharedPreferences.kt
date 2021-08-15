package com.example.cryptowallet.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.cryptowallet.network.classesapi.AccessToken
import com.google.gson.Gson

class EncSharedPreferences() {
    companion object{
        private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        private var INSTANCE : SharedPreferences ?= null

        private fun encryptedSharedPreferencesInstance(context:Context): SharedPreferences? {
            var instance = INSTANCE
            if (instance == null) {
                instance =
                    EncryptedSharedPreferences.create(
                        "shared_preferences_filename",
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                INSTANCE = instance
            }
            return instance
        }

        fun saveToEncryptedSharedPrefsString(KEY_NAME: String, value: String,context: Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences?.edit()
            editor?.putString(KEY_NAME, value)
            editor?.apply()
        }

        fun getValueString(KEY_NAME: String,context: Context): String? {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            return encSharedPreferences?.getString(KEY_NAME, null)
        }

        fun convertTestClassToJsonString(classObj:AccessToken):String{
            var gson = Gson()
            return gson.toJson(classObj)
        }

        fun convertJsonStringToTestClass(stringObj:String):AccessToken{
            var gson = Gson()
            return gson.fromJson(stringObj,AccessToken::class.java)
        }

        fun removeValueFromEncShareDPrefs(keyString: String,context: Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences?.edit()
            editor?.remove(keyString)
            editor?.apply()
        }

        fun clearSharedPreference(context:Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences?.edit()
            editor?.clear()
            editor?.apply()
        }
    }
}