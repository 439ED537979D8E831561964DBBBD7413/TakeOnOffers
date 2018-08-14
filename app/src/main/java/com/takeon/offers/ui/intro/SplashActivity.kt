package com.takeon.offers.ui.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.takeon.offers.R
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.ui.MainActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.ui.loginregister.LoginActivity
import com.takeon.offers.utils.CommonDataUtility

class SplashActivity : BaseActivity() {

  private var activity: Activity? = null
  private val handler = Handler()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_splash)


    activity = this@SplashActivity
  }

  /* Check if location enable or not*/
  override fun onStart() {
    super.onStart()
    if (CommonDataUtility.isLocationProviderEnabled(activity!!)) {
      if (MyApplication.instance
              .preferenceUtility
              .getString("slider_shown") == "y"
      ) {
        handler.postDelayed(startActivityRunnable, 2000)
      } else {
        handler.postDelayed(sliderActivityRunnable, 2000)
      }
    } else {
      handler.post(postLocationDialog)
    }
  }

  /* Runnable to start slider activity */
  private var sliderActivityRunnable: Runnable = object : Runnable {
    override fun run() {
      handler.removeCallbacks(this)
      startActivity(Intent(activity, SliderActivity::class.java))
      finish()
    }
  }

  /* Runnable to start either main or login activity */
  private var startActivityRunnable: Runnable = object : Runnable {
    override fun run() {
      handler.removeCallbacks(this)

      if (CommonDataUtility.checkConnection(this@SplashActivity)) {
        if (MyApplication.instance.preferenceUtility.login) {
          startActivity(Intent(activity, MainActivity::class.java))
        } else {
          startActivity(Intent(activity, LoginActivity::class.java))
        }
        finish()

      } else {
        handler.post(postInternetDialog)
      }
    }
  }

  /* Runnable to start internet enable setting page */
  private var postInternetDialog: Runnable = object : Runnable {

    override fun run() {

      handler.removeCallbacks(this)
      val builder = MaterialDialog.Builder(activity!!)
          .title(R.string.internet_title)
          .content(R.string.internet_msg)
          .positiveText(R.string.btn_ok)
          .onPositive { _, _ ->
            try {
              finish()
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }

      val dialog = builder.build()
      dialog.setCancelable(false)
      dialog.setCanceledOnTouchOutside(false)
      dialog.show()
    }
  }

  /* Runnable to start location enable setting page */
  private var postLocationDialog: Runnable = object : Runnable {

    override fun run() {

      handler.removeCallbacks(this)
      val builder = MaterialDialog.Builder(activity!!)
          .title(R.string.location_title)
          .content(R.string.location_msg)
          .positiveText(R.string.btn_settings)
          .negativeText(R.string.btn_cancel)
          .onPositive { _, _ ->
            try {
              val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
              startActivityForResult(intent, 101)
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
          .onNegative { _, _ -> finish() }

      val dialog = builder.build()
      dialog.setCancelable(false)
      dialog.setCanceledOnTouchOutside(false)
      dialog.show()
    }
  }
}
