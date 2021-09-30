package com.example.cryptowallet.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.cryptowallet.network.classesapi.AccessToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class EncSharedPreferences {
    companion object{
        private var masterKeyAlias:MasterKey ?=null
        private var INSTANCE : SharedPreferences ?= null

        private fun encryptedSharedPreferencesInstance(context:Context): SharedPreferences {
            masterKeyAlias = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            var instance = INSTANCE
            if (instance == null) {
                instance =
                    EncryptedSharedPreferences.create(
                        context,
                        "shared_preferences_filename",
                        masterKeyAlias!!,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                INSTANCE = instance
            }
            return instance
        }

        fun saveToEncryptedSharedPrefsString(KEY_NAME: String, value: String,context: Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences.edit()
            editor?.putString(KEY_NAME, value)
            editor?.apply()
        }

        fun getValueString(KEY_NAME: String,context: Context): String? {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            return encSharedPreferences.getString(KEY_NAME, null)
        }

        fun convertTestClassToJsonString(classObj: AccessToken): String {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<AccessToken> =
                moshi.adapter(AccessToken::class.java)
            return jsonAdapter.toJson(classObj)
        }

        fun convertJsonStringToTestClass(stringObj:String): AccessToken? {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<AccessToken> =
                moshi.adapter(AccessToken::class.java)
            return jsonAdapter.fromJson(stringObj)
        }

        fun removeValueFromEncShareDPrefs(keyString: String,context: Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences.edit()
            editor?.remove(keyString)
            editor?.apply()
        }

        fun clearSharedPreference(context:Context) {
            val encSharedPreferences = encryptedSharedPreferencesInstance(context)
            val editor = encSharedPreferences.edit()
            editor?.clear()
            editor?.apply()
        }
    }
}