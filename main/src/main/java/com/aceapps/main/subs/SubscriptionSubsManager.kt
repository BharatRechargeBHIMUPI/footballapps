package com.aceapps.main.subs

interface SubscriptionSubsManager {

    interface Callback {
        fun activePurchase(productID : String)
        fun unActivePurchase()
    }

    fun init(callback: Callback)
}