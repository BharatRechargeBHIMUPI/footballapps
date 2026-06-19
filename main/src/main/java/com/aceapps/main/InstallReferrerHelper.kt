package com.aceapps.main

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

class InstallReferrerHelper(private val context: Context) {

    private var referrerClient: InstallReferrerClient? = null

    fun getInstallReferrer(onReferrerReceived: (ReferrerDetails?) -> Unit) {
        referrerClient = InstallReferrerClient.newBuilder(context).build()

        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response: ReferrerDetails? = referrerClient?.installReferrer
                            onReferrerReceived(response)
                            logReferrer(response)
                        } catch (e: RemoteException) {
                            Log.e("InstallReferrer", "RemoteException", e)
                            onReferrerReceived(null)
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.w("InstallReferrer", "API not supported")
                        onReferrerReceived(null)
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.w("InstallReferrer", "Service unavailable")
                        onReferrerReceived(null)
                    }
                    else -> onReferrerReceived(null)
                }
                referrerClient?.endConnection()
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to reconnect if needed
            }
        })
    }

    private fun logReferrer(details: ReferrerDetails?) {
        details?.let {
            Log.d("InstallReferrer", """
                Referrer: ${it.installReferrer}
                Click Timestamp: ${it.referrerClickTimestampSeconds}
                Install Timestamp: ${it.installBeginTimestampSeconds}
                Google Play Instant: ${it.googlePlayInstantParam}
            """.trimIndent())
        }
    }
}