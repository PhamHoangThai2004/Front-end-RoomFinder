package com.pht.roomfinder.helper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object Secure {

    private const val PASSWORD = "password"
    private const val EMAIL = "email"
    private const val KEY = "encrypted_password"

    private fun encrypt(context: Context) =
        EncryptedSharedPreferences.create(
            "secure",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun encryptPassword(context: Context, password: String) {
        val encrypt = encrypt(context)
        with(encrypt.edit()) {
            putString(KEY, password)
            apply()
        }
    }

    fun decryptPassword(context: Context): String? {
        val encrypt = encrypt(context)
        return encrypt.getString(KEY, null)
    }

    fun save(context: Context, email: String?, password: String?) {
        val encrypt = encrypt(context)
        with(encrypt.edit()) {
            putString(EMAIL, email)
            putString(PASSWORD, password)
            apply()
        }
    }

    fun get(context: Context): Pair<String?, String?> {
        val encrypt = encrypt(context)
        val email = encrypt.getString(EMAIL, null)
        val password = encrypt.getString(PASSWORD, null)
        return Pair(email, password)
    }

    fun clear(context: Context) {
        val encrypt = encrypt(context)
        with(encrypt.edit()) {
            remove(EMAIL)
            remove(PASSWORD)
            remove(KEY)
            apply()
        }
    }
}