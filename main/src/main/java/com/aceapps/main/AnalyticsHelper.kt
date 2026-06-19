package com.aceapps.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics


class AnalyticsHelper private constructor() {

    companion object {
        val TAG: String = AnalyticsHelper::class.java.canonicalName!!

        @Volatile
        private var instance: AnalyticsHelper? = null

        fun getInstance(): AnalyticsHelper =
            instance ?: synchronized(this) {
                instance ?: AnalyticsHelper().also { instance = it }
            }
    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var fbAnalytics: AppEventsLogger? = null

    fun initialize(mContext: Context) {
        fbAnalytics = AppEventsLogger.newLogger(mContext)
    }



    fun logEvent(eventName: String, params: Map<String, String>?) {
        Log.d(TAG, "logEvent, event = $eventName")

        val bundle = Bundle().apply {
            params?.forEach { (key, value) ->
                putString(key, value)
            }
        }

        logFbEvent(eventName, bundle)
    }


    private fun logFbEvent(eventName: String, eventParameters: Bundle) {

        try {
            fbAnalytics!!.logEvent(eventName, eventParameters);
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
