package com.takeon.offers.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.takeon.offers.kprogresshud.KProgressHUD
import com.takeon.offers.ui.MainActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.utils.VolleyNetWorkCall
import java.util.ArrayList

/**
 * Created by Developer on 2/15/2017.
 */

open class BaseFragment : Fragment() {

  lateinit var activity: MainActivity
  lateinit var netWorkCall: VolleyNetWorkCall
  var progressHUD: KProgressHUD? = null

  val userId: String
    get() = MyApplication.instance.preferenceUtility.userId

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activity = (getActivity() as MainActivity?)!!
    netWorkCall = VolleyNetWorkCall()
  }

  fun locationPermission(): Boolean {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val readPermission = ContextCompat.checkSelfPermission(
          activity, Manifest.permission.ACCESS_FINE_LOCATION
      )
      val writePermission = ContextCompat.checkSelfPermission(
          activity, Manifest.permission.ACCESS_COARSE_LOCATION
      )

      val listPermissionsNeeded = ArrayList<String>()
      if (readPermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
      }
      if (writePermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
      }

      return if (!listPermissionsNeeded.isEmpty()) {
        requestPermissions(listPermissionsNeeded.toTypedArray(), 101)
        false
      } else {
        true
      }
    }
    return true
  }
}
