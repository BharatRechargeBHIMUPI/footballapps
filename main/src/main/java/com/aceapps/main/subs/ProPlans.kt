package com.aceapps.main.subs

import com.android.billingclient.api.ProductDetails
import com.revenuecat.purchases.models.StoreProduct

object ProPlans {
    //remote config enabled product only
    var correctScore: MutableList<StoreProduct> = mutableListOf()
    var twoOdds: MutableList<StoreProduct> = mutableListOf()
    var mega_subscription: MutableList<StoreProduct> = mutableListOf()
    var fiveOdds: MutableList<StoreProduct> = mutableListOf()
    var tenOdds: MutableList<StoreProduct> = mutableListOf()
    var ft: MutableList<StoreProduct> = mutableListOf()
    var htft: MutableList<StoreProduct> = mutableListOf()
    var allProduct: MutableList<StoreProduct> = mutableListOf()
    var activeSubscription: MutableList<String> = mutableListOf()

}