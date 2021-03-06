package com.takeon.offers.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.R.layout
import com.takeon.offers.adapter.BusinessListAdapter
import com.takeon.offers.kprogresshud.KProgressHUD
import com.takeon.offers.model.BusinessResponse
import com.takeon.offers.model.SingleBusinessData
import com.takeon.offers.ui.business.BusinessDetailsActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.LocationService
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_fragment_favorite.llRoot
import kotlinx.android.synthetic.main.takeon_fragment_favorite.recyclerViewFavoriteList
import kotlinx.android.synthetic.main.takeon_fragment_favorite.toolbar_title
import kotlinx.android.synthetic.main.takeon_layout_error.btnRetry
import kotlinx.android.synthetic.main.takeon_layout_error.llErrorLayout
import kotlinx.android.synthetic.main.takeon_layout_error.txtMessage
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class FavoriteFragment : BaseFragment(),
    VolleyNetWorkCall.OnResponse,
    BusinessListAdapter.ClickListener,
    LocationService.LocationGet {

  private var businessArrayList: ArrayList<SingleBusinessData>? = null
  private var listAdapter: BusinessListAdapter? = null
  private var locationService: LocationService? = null
  private var currentLatitude = 0.0
  private var currentLongitude = 0.0
  private var favoritePosition = -1

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(layout.takeon_fragment_favorite, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    initUI()
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

    try {

      when (url) {

      // <editor-fold API_GET_FAVORITE_BUSINESSES>
        StaticDataUtility.API_GET_FAVORITE_BUSINESSES -> {

          hideProgressBar()

          val businessResponseModel = GsonBuilder().create()
              .fromJson(response, BusinessResponse::class.java)

          if (businessResponseModel.status.equals("true", ignoreCase = true)) {

            for (business in businessResponseModel.businesses!!) {
              businessArrayList!!.add(business)
            }

            if (businessArrayList!!.size > 0) {

              llErrorLayout!!.visibility = View.GONE

              recyclerViewFavoriteList!!.visibility = View.VISIBLE
              listAdapter = BusinessListAdapter(activity, businessArrayList!!, this)
              recyclerViewFavoriteList!!.adapter = listAdapter
            } else {
              showNoDataLayout("noData", getString(R.string.str_no_favorite_business))
            }

          } else {
            showNoDataLayout("noData", businessResponseModel.message!!)
          }
        }
      // </editor-fold>

      // <editor-fold API_REMOVE_FROM_FAVORITE_BUSINESS>
        StaticDataUtility.API_REMOVE_FROM_FAVORITE_BUSINESS -> {

          hideProgressBar()

          val businessResponseModel = GsonBuilder().create()
              .fromJson(response, BusinessResponse::class.java)

          if (businessResponseModel.status.equals("true", ignoreCase = true)) {
            businessArrayList!!.removeAt(favoritePosition)
            listAdapter!!.updateData(favoritePosition)

            if (businessArrayList!!.size == 0) {
              showNoDataLayout("noData", getString(R.string.str_no_favorite_business))
            }
          } else {
            CommonDataUtility.showSnackBar(llRoot, businessResponseModel.message)
          }

        }
      // </editor-fold>
      }

      // </editor-fold>
    } catch (e: Exception) {
      e.printStackTrace()
      hideProgressBar()
      CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
    }
  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {

    hideProgressBar()

    try {

      if (url.equals(
              StaticDataUtility.API_GET_FAVORITE_BUSINESSES, ignoreCase = true
          )
      ) {
        showNoDataLayout("error", getString(R.string.str_no_favorite_business_error))
      } else {
        showNoDataLayout("error", getString(R.string.error_server))
      }
      //txtMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.small_app_logo, 0, 0);
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun showNoDataLayout(
    type: String,
    message: String
  ) {

    btnRetry.visibility = if (type == "error") View.GONE else View.VISIBLE

    llErrorLayout!!.visibility = View.VISIBLE
    recyclerViewFavoriteList!!.visibility = View.GONE

    txtMessage!!.text = message
  }

  override fun onBusinessClick(position: Int) {
    val intent = Intent(activity, BusinessDetailsActivity::class.java)
    intent.putExtra("business_id", businessArrayList!![position].id)
    startActivity(intent)
  }

  override fun onFavoriteClick(
    position: Int,
    business_id: String,
    type: String
  ) {
    favoritePosition = position
    addRemoveFavorite(business_id)
  }

  override fun onLocationGet(location: Location?) {

    showProgressBar()

    if (location != null) {
      currentLatitude = location.latitude
      currentLongitude = location.longitude
    }

    getFavoriteBusiness()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {

      101 -> {

        val perms = HashMap<String, Int>()
        // Initial
        perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager
            .PERMISSION_GRANTED
        perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager
            .PERMISSION_GRANTED

        // Fill with results
        for (i in permissions.indices)
          perms[permissions[i]] = grantResults[i]

        val fineLocation =
          perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
        val coarseLocation =
          perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED

        if (fineLocation && coarseLocation) {
          updateLocation()
        } else {
          CommonDataUtility.showSnackBar(llRoot, getString(R.string.location_permission))

          if (locationPermission()) {
            updateLocation()
          }
        }
      }
    }
  }

  private fun initUI() {

    toolbar_title.text = getString(R.string.text_favorite)

    progressHUD = KProgressHUD.create(activity)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setCancellable(false)

    recyclerViewFavoriteList!!.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    recyclerViewFavoriteList!!.isNestedScrollingEnabled = false

    if (locationPermission()) {
      updateLocation()
    }

    btnRetry.setOnClickListener {
      if (locationPermission()) {
        updateLocation()
      }
    }
  }

  private fun updateLocation() {

    if (locationService != null) {
      locationService!!.stopLocationUpdates()
      locationService!!.stop()
    }

    locationService = LocationService("Location Update", this)
    locationService!!.isOneTime = false
    locationService!!.setInterval((5 * 1000).toLong())
    locationService!!.setFastestInterval((5 * 1000).toLong())
    locationService!!.init(activity)
  }

  private fun getFavoriteBusiness() {

    showProgressBar()

    businessArrayList = ArrayList()

    llErrorLayout!!.visibility = View.GONE
    recyclerViewFavoriteList!!.visibility = View.VISIBLE

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = userId
      jsonObject["latitude"] = currentLatitude.toString()
      jsonObject["longitude"] = currentLongitude.toString()
    } catch (e: Exception) {
      e.printStackTrace()
    }

    netWorkCall.makePostServiceCall(
        activity, StaticDataUtility.API_GET_FAVORITE_BUSINESSES, this, jsonObject
    )
  }

  private fun showProgressBar() {
    progressHUD!!.show()
  }

  fun hideProgressBar() {
    if (progressHUD != null) {
      progressHUD!!.dismiss()
    }
  }

  private fun addRemoveFavorite(business_id: String) {

    showProgressBar()

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = userId
      jsonObject["business_id"] = business_id
    } catch (e: Exception) {
      e.printStackTrace()
    }

    netWorkCall.makePostServiceCall(
        activity, StaticDataUtility.API_REMOVE_FROM_FAVORITE_BUSINESS, this, jsonObject
    )
  }
}
