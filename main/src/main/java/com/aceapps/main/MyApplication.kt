package com.aceapps.main

import android.app.Application
import android.util.Log
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        FirebaseApp.initializeApp(this)
        AnalyticsHelper.getInstance().initialize(this)
        FirebaseApp.initializeApp(this)
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(PurchasesConfiguration.Builder(this, getString(R.string.revenue_cat_key)).build())

    }
}