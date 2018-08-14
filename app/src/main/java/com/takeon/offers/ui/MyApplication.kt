package com.takeon.offers.ui

import android.app.Application
import com.takeon.offers.utils.SharedPreferenceUtility

/**
 * Created by admin on 27/01/18.
 */

class MyApplication : Application() {

  @get:Synchronized lateinit var preferenceUtility: SharedPreferenceUtility

  override fun onCreate() {
    super.onCreate()
    instance = this
    preferenceUtility = SharedPreferenceUtility(instance)
  }

  companion object {

    @get:Synchronized lateinit var instance: MyApplication
  }
}
