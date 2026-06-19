package com.aceapps.main

import android.app.Activity
import android.content.SharedPreferences

class SharedPref(private val activity: Activity) {
    private val sharedPref: SharedPreferences =
        activity.getSharedPreferences(activity.getString(R.string.app_name), 0)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    private val IS_ONBOARDING_SHOWN = "isOnBoardingShown"
    private val DAILY_USED_COUNT = "dailyUsedCount"
    private val DAILY_AVAILABLE_COUNT = "dailyAvailableCount"
    private val DAILY_COUNT_LIMIT = "dailyCountLimit"
    private val IS_DAILY_LIMIT_USED = "isDailyLimitUsed"
    private val SAVED_DATE = "savedDate"
    private val REMOTE_CONFIG_VALUE = "remoteConfigValue"
    private val USER_DETAILS = "userDetails"
    private val IS_PREMIUM = "isPremium"
    private val PRODUCT_ID = "productID"
    private val FIREBASE_UID = "firebaseUID"
    private val IS_USER_SIGN_IN = "isUserSignIn"

    private val USER_PHOTO_URL = "userPhotoURL"
    private val USER_FULL_NAME = "userFullName"
    private val USER_EMAIL = "userEmail"
    private val IS_HISTORY_SYNC = "isHistorySync"
    private val CAMERA_PERMISSION_DECLINE = "isCameraDecline"
    private val STORAGE_PERMISSION_DECLINE = "isStorageDecline"


    fun isCameraDecline(value: Boolean) {
        editor.putBoolean(CAMERA_PERMISSION_DECLINE, value)
        editor.commit()
    }

    fun isCameraDecline(): Boolean {
        return sharedPref.getBoolean(CAMERA_PERMISSION_DECLINE, false)
    }

    fun isStorageDecline(value: Boolean) {
        editor.putBoolean(STORAGE_PERMISSION_DECLINE, value)
        editor.commit()
    }

    fun isStorageDecline(): Boolean {
        return sharedPref.getBoolean(STORAGE_PERMISSION_DECLINE, false)
    }


    fun isHistorySync(value: Boolean) {
        editor.putBoolean(IS_HISTORY_SYNC, value)
        editor.commit()
    }

    fun isHistorySync(): Boolean {
        return sharedPref.getBoolean(IS_HISTORY_SYNC, false)
    }


    fun userPhotoURL(value: String) {
        editor.putString(USER_PHOTO_URL, value)
        editor.commit()
    }

    fun userPhotoURL(): String {
        return sharedPref.getString(USER_PHOTO_URL, "")!!
    }

    fun userFullName(value: String) {
        editor.putString(USER_FULL_NAME, value)
        editor.commit()
    }

    fun userFullName(): String {
        return sharedPref.getString(USER_FULL_NAME, "")!!
    }


    fun userEmail(value: String) {
        editor.putString(USER_EMAIL, value)
        editor.commit()
    }

    fun userEmail(): String {
        return sharedPref.getString(USER_EMAIL, "")!!
    }

    fun isUserSignIn(value: Boolean) {
        editor.putBoolean(IS_USER_SIGN_IN, value)
        editor.commit()
    }

    fun isUserSignIn(): Boolean {
        return sharedPref.getBoolean(IS_USER_SIGN_IN, false)!!
    }

    fun firebaseUID(value: String) {
        editor.putString(FIREBASE_UID, value)
        editor.commit()
    }

    fun firebaseUID(): String {
        return sharedPref.getString(FIREBASE_UID, "")!!
    }

    fun productID(value: String) {
        editor.putString(PRODUCT_ID, value)
        editor.commit()
    }

    fun productID(): String {
        return sharedPref.getString(PRODUCT_ID, "")!!
    }

    fun userDetails(value: String) {
        editor.putString(USER_DETAILS, value)
        editor.commit()
    }

    fun userDetails(): String {
        return sharedPref.getString(USER_DETAILS, "")!!
    }

    fun savedDate(value: String) {
        editor.putString(SAVED_DATE, value)
        editor.commit()
    }

    fun savedDate(): String {
        return sharedPref.getString(SAVED_DATE, "")!!
    }

    fun remoteConfigValues(value: String) {
        editor.putString(REMOTE_CONFIG_VALUE, value)
        editor.commit()
    }

    fun remoteConfigValues(): String {
        return sharedPref.getString(REMOTE_CONFIG_VALUE, "")!!
    }

    fun isPremium(value: Boolean) {
        editor.putBoolean(IS_PREMIUM, value)
        editor.commit()
    }

    fun isPremium(): Boolean {
        return sharedPref.getBoolean(IS_PREMIUM, false)
    }
    fun isOnBoardingShown(value: Boolean) {
        editor.putBoolean(IS_ONBOARDING_SHOWN, value)
        editor.commit()
    }

    fun isOnBoardingShown(): Boolean {
        return sharedPref.getBoolean(IS_ONBOARDING_SHOWN, false)
    }

    fun isDailyLimitUsed(value: Boolean) {
        editor.putBoolean(IS_DAILY_LIMIT_USED, value)
        editor.commit()
    }

    fun isDailyLimitUsed(): Boolean {
        return sharedPref.getBoolean(IS_DAILY_LIMIT_USED, false)
    }

    fun dailyUsedCount(value: Long) {
        editor.putLong(DAILY_USED_COUNT, value)
        editor.commit()
    }

    fun dailyUsedCount(): Long {
        return sharedPref.getLong(DAILY_USED_COUNT, 0L)
    }

    fun dailyAvailableCount(value: Long) {
        editor.putLong(DAILY_AVAILABLE_COUNT, value)
        editor.commit()
    }

    fun dailyAvailableCount(): Long {
        return sharedPref.getLong(DAILY_AVAILABLE_COUNT, 0L)
    }

    fun dailyCountLimit(value: Long) {
        editor.putLong(DAILY_COUNT_LIMIT, value)
        editor.commit()
    }

    fun dailyCountLimit(): Long {
        return sharedPref.getLong(DAILY_COUNT_LIMIT, 0L)
    }
}