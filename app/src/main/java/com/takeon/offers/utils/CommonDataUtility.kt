package com.takeon.offers.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.support.annotation.Nullable
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.common.util.Strings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.takeon.offers.model.CityModel
import com.takeon.offers.ui.MyApplication
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by admin on 25/01/18.
 */

object CommonDataUtility {

  fun checkConnection(activity: Activity): Boolean {

    val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
  }

  fun getPreferenceString(key: String): String {
    return MyApplication.instance.preferenceUtility.getString(key)
  }

  fun setPreferenceString(
    key: String,
    value: String
  ) {
    MyApplication.instance.preferenceUtility.setString(key, value)
  }

  fun showSnackBar(
    view: View,
    message: String?
  ) {
    Snackbar.make(view, message!!, Snackbar.LENGTH_LONG)
        .show()
  }

  @SuppressLint("ServiceCast")
  fun hideKeyboardFrom(
    context: Context,
    view: View
  ) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }

  fun isLocationProviderEnabled(context: Context): Boolean {
    return isGPSProviderEnabled(context)
  }

  private fun isGPSProviderEnabled(context: Context): Boolean {
    val locationManager = context
        .getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
  }

  fun isValidMobile(phone: String): Boolean {
    return !Pattern.matches("[a-zA-Z]+", phone) && phone.length >= 6 && phone.length <= 13
  }

  fun isValidMail(email: String): Boolean {
    val check: Boolean
    val p: Pattern
    val m: Matcher

    val emailString =
      "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    p = Pattern.compile(emailString)

    m = p.matcher(email)
    check = m.matches()

    return check
  }

  /**
   *  The setArrayListPreference method used for
   *  store arrayList in shred preference
   */
  fun setArrayListPreference(
    @Nullable arrayList: MutableList<CityModel>,
    key: String
  ) {
    MyApplication.instance.preferenceUtility.setString(key, Gson().toJson(arrayList))
  }

  /**
   *  The getArrayListPreference method used for
   *  get stored shred preference arrayList
   */
  fun getArrayListPreference(
    key: String
  ): ArrayList<CityModel> {

    val list: ArrayList<CityModel>

    val json =
      MyApplication.instance.preferenceUtility.getString(key)
    val type = object : TypeToken<ArrayList<CityModel>>() {
    }.type

    list = if (!Strings.isEmptyOrWhitespace(json)) {
      Gson().fromJson(json, type)
    } else {
      ArrayList()
    }

    return list
  }

}
