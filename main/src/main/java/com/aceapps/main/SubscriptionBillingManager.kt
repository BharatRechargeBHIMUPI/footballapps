package com.aceapps.main

interface SubscriptionBillingManager {

    interface Callback {
        fun onPlanRestored()
        fun onPlanRestoreFail()
    }

    fun init(callback: Callback)
}