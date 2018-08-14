package com.takeon.offers.utils

import android.app.Activity
import android.content.SharedPreferences
import com.takeon.offers.ui.MyApplication

/**
 * Created by admin on 27/01/18.
 */

class SharedPreferenceUtility(activity: MyApplication) {
  private val appSharedPrefs: SharedPreferences

  init {
    this.appSharedPrefs = activity.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE)
  }

  var login: Boolean
    get() = appSharedPrefs.getBoolean("isLogin", false)
    set(value) {
      appSharedPrefs.edit()
          .putBoolean("isLogin", value)
          .apply()
    }

  var token: String
    get() = appSharedPrefs.getString("token", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("token", value)
          .apply()
    }

  var userId: String
    get() = appSharedPrefs.getString("user_id", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("user_id", value)
          .apply()
    }

  var mobileNumber: String
    get() = appSharedPrefs.getString("mobile_number", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("mobile_number", value)
          .apply()
    }

  var photo: String
    get() = appSharedPrefs.getString("user_photo", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("user_photo", value)
          .apply()
    }

  var email: String
    get() = appSharedPrefs.getString("email", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("email", value)
          .apply()
    }

  var firstName: String
    get() = appSharedPrefs.getString("first_name", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("first_name", value)
          .apply()
    }

  var lastName: String
    get() = appSharedPrefs.getString("last_name", "")
    set(value) {
      appSharedPrefs.edit()
          .putString("last_name", value)
          .apply()
    }

  fun clearData() {
    appSharedPrefs.edit()
        .clear()
        .apply()
  }

  fun setString(
    key: String,
    value: String
  ) {
    appSharedPrefs.edit()
        .putString(key, value)
        .apply()
  }

  fun getString(key: String): String {
    return appSharedPrefs.getString(key, "")
  }

  companion object {

    private const val USER_PREFS = "TakeOnOffers_pref"
  }
}
