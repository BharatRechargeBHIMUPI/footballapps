package com.aceapps.main.subs

import android.app.Activity
import android.util.Log
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.StoreProduct

class RevenueCatSyncPlans(private val activity: Activity) : SubscriptionPlanSync {


    private lateinit var listProductDetails: MutableList<StoreProduct>

    private lateinit var syncPlanCallBack: SubscriptionPlanSync.Callback

    override fun init(callback: SubscriptionPlanSync.Callback) {
        this.syncPlanCallBack = callback
        initBillingClient()
    }


    fun initBillingClient() {
        listProductDetails = mutableListOf()

        Purchases.sharedInstance.getOfferingsWith({ error ->
            syncPlanCallBack.onPlanFetchedFail("Subscription product fetch failed ${error.message}")

        }) { offerings ->
            val packages = offerings.current?.availablePackages ?: return@getOfferingsWith

            for (pkg in packages) {
                val product = pkg.product
                listProductDetails.add(product)
                ProPlans.allProduct.add(product)

                if (product.id.contains("correct_score")){
                    ProPlans.correctScore.add(product)
                }else if (product.id.contains("htft")){
                    ProPlans.htft.add(product)
                }else if (product.id.contains("ft_draws")){
                    ProPlans.ft.add(product)
                }else if (product.id.contains("ten_odds")){
                    ProPlans.tenOdds.add(product)
                }else if (product.id.contains("five_odds")){
                    ProPlans.fiveOdds.add(product)
                }else if (product.id.contains("two_odds")){
                    ProPlans.twoOdds.add(product)
                }else if (product.id.contains("mega_subscription")){
                    ProPlans.mega_subscription.add(product)
                }
            }


            syncPlanCallBack.onPlanFetchedSuccessfully()
        }

    }
}