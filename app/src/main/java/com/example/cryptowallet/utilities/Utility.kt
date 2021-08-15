package com.example.cryptowallet.utilities

import android.app.Application

class Utility: Application(){
    companion object {
        private var instance: Application? = null

        fun getInstance(): Application? {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}