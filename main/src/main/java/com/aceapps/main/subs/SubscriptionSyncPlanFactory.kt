package com.aceapps.main.subs

import android.app.Activity
import androidx.lifecycle.LifecycleCoroutineScope

object SubscriptionSyncPlanFactory {

    fun create(
        activity: Activity
    ): SubscriptionPlanSync {

        return RevenueCatSyncPlans(activity)
    }
}