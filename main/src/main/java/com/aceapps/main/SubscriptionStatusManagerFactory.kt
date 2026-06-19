package com.aceapps.main

import android.app.Activity
import androidx.lifecycle.LifecycleCoroutineScope
import com.aceapps.main.subs.SubscriptionSubsManager

//import com.ai.lib_revenuecat_android_v2.subscription.RevenueCatSubManager

object SubscriptionStatusManagerFactory {

    fun create(
    ): SubscriptionSubsManager {

        return RevenueCatSubManager()
    }
}