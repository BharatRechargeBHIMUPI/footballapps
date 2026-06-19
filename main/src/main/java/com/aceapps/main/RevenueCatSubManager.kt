package com.aceapps.main


import android.app.Activity
import com.aceapps.main.subs.ProPlans
import com.aceapps.main.subs.SubscriptionSubsManager
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.restorePurchasesWith

// TODO: is class is for check , weather user has any active purchase or not
class RevenueCatSubManager(
): SubscriptionSubsManager {

    private lateinit var callback: SubscriptionSubsManager.Callback


    override fun init(callback: SubscriptionSubsManager.Callback) {
        this.callback = callback
        initBillingClient()
    }
    fun initBillingClient() {
        Purchases.sharedInstance.restorePurchasesWith(
            { error ->
                callback.unActivePurchase()
            },
            { customerInfo ->

                val activeSubscription : MutableList<String> = mutableListOf()

                customerInfo.activeSubscriptions.forEach { productId ->
                    activeSubscription.add(productId.substringBefore(":"))
                    ProPlans.activeSubscription.add(productId.substringBefore(":"))
                }

                if(activeSubscription.isEmpty()){
                    callback.unActivePurchase()

                }else{
                    val latestProduct = activeSubscription[activeSubscription.size-1]
                    callback.activePurchase(latestProduct)
                }
            }
        )


    }
}
