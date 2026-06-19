package com.aceapps.main.subs

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.common.collect.ImmutableList
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith


class LaunchFlow(private val activity: Activity, private val launchFlowCallback: LaunchFlowCallback) {

    private lateinit var productDetails: StoreProduct


    fun initBillingClient(product: StoreProduct) {



        this.productDetails = product

        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(activity, product).build(),
            onError = { error, userCancelled ->
                launchFlowCallback.onFailedPurchase()
            },
            onSuccess = { storeTransaction, customerInfo ->
                if (customerInfo.entitlements["pro"]?.isActive == true) {
                    launchFlowCallback.onSuccessfulPurchase(storeTransaction!!, productDetails.type.name.lowercase())
                }else{
                    launchFlowCallback.onFailedPurchase()
                }
            }
        )
    }

    interface LaunchFlowCallback{
        fun onSuccessfulPurchase(purchase: StoreTransaction, productType :String)
        fun onFailedPurchase()
    }
}