package com.aceapps.main.subs

interface SubscriptionPlanSync {

    interface Callback {
        fun onPlanFetchedSuccessfully()
        fun onPlanFetchedFail(error: String)
    }

    fun init(callback: Callback)
}
