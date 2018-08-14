package com.takeon.offers.ui.business

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.adapter.AreaListAdapter
import com.takeon.offers.adapter.BusinessListAdapter
import com.takeon.offers.adapter.CityListAdapter
import com.takeon.offers.adapter.CityListAdapter.CityClickListener
import com.takeon.offers.model.BusinessModel
import com.takeon.offers.model.CityModel
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.LocationService
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_business.btnFilter
import kotlinx.android.synthetic.main.takeon_activity_business.imgLogo
import kotlinx.android.synthetic.main.takeon_activity_business.llRoot
import kotlinx.android.synthetic.main.takeon_activity_business.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_business.recyclerViewBusinessList
import kotlinx.android.synthetic.main.takeon_activity_business.toolbar_title
import kotlinx.android.synthetic.main.takeon_activity_business.txtCity
import kotlinx.android.synthetic.main.takeon_activity_business.txtDistance
import kotlinx.android.synthetic.main.takeon_layout_error.btnRetry
import kotlinx.android.synthetic.main.takeon_layout_error.llErrorLayout
import kotlinx.android.synthetic.main.takeon_layout_error.txtMessage
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BusinessActivity : BaseActivity(),
    View.OnClickListener,
    VolleyNetWorkCall.OnResponse,
    BusinessListAdapter.ClickListener,
    LocationService.LocationGet {

  private var activity: Activity? = null
  private var businessArrayList: ArrayList<BusinessModel>? = null
  private var cityArrayList: ArrayList<CityModel>? = null
  private var areaArrayList: ArrayList<CityModel>? = null
  private var listAdapter: BusinessListAdapter? = null
  private lateinit var areaListAdapter: AreaListAdapter
  private var locationService: LocationService? = null
  private var currentLatitude = 0.0
  private var currentLongitude = 0.0
  private var categoryId = ""
  private var offset = 10
  private var cityId = ""
  private var areaName = ""
  private var distance = ""
  private var favoritePosition = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_business)

    activity = this@BusinessActivity

    setToolBar()
    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.txtCity -> {

        if (cityArrayList!!.size > 0) {
          showCitiesDialog()
        } else {
          getCities()
        }
      }

      R.id.txtDistance -> {
        showList()
      }

      R.id.btnFilter -> {

        when {
          areaName == "" -> CommonDataUtility.showSnackBar(
              llRoot, "Please select area name using city"
          )
          distance == "" -> CommonDataUtility.showSnackBar(llRoot, "Please select distance")
          else -> {
            getAllBusiness("filter")
          }
        }
      }
    }
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

    hideProgressBar()

    try {

      when (url) {

      // <editor-fold API_GET_CITIES>
        StaticDataUtility.API_GET_CITIES -> {
          val citiesArray = response.optJSONArray("cities")

          if (response.optString("status").equals("true", ignoreCase = true)) {

            for (i in 0 until citiesArray.length()) {

              val cityJsonObject = citiesArray.optJSONObject(i)

              val cityModel = CityModel()
              cityModel.cityId = cityJsonObject.optString("id")
              cityModel.cityName = cityJsonObject.optString("city")

              cityArrayList!!.add(cityModel)
            }

            CommonDataUtility.setArrayListPreference(cityArrayList!!, "cityList")
            showCitiesDialog()

          } else {
            CommonDataUtility.showSnackBar(llRoot, response.optString("message"))
          }
        }
      }
      // </editor-fold>

    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

    try {

      when (url) {

      // <editor-fold API_GET_BUSINESSES>
        StaticDataUtility.API_GET_BUSINESSES -> {

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

            val jsonArray = jsonObject.getJSONArray("businesses")

            for (i in 0 until jsonArray.length()) {

              val businessesObject = jsonArray.getJSONObject(i)

              val businessModel = BusinessModel()
              businessModel.businessId = businessesObject.optString("id")
              businessModel.businessName = businessesObject.optString("name")
              businessModel.city = businessesObject.optString("city")
              businessModel.categoryId = businessesObject.optString("category_id")
              businessModel.categoryName = businessesObject.optString("category")
              businessModel.subCategoryId = businessesObject.optString("sub_category_id")
              businessModel.subCategoryName = businessesObject.optString("sub_categories")
              businessModel.savingAmount = businessesObject.optString("total_saving_amount")
              businessModel.totalRedeem = businessesObject.optString("total_reedem")
              businessModel.photo = businessesObject.optString("photo")
              businessModel.distance = businessesObject.optString("distance_in_km")
              businessModel.isFavorite = businessesObject.optString("is_faviorite")
              businessModel.totalOffers = businessesObject.optString("total_offers")
              businessModel.cuisines = businessesObject.optString("cosiness")

              businessArrayList!!.add(businessModel)
            }

            if (businessArrayList!!.size > 0) {

              llErrorLayout!!.visibility = View.GONE
              recyclerViewBusinessList!!.visibility = View.VISIBLE

              listAdapter = BusinessListAdapter(this, businessArrayList!!, this)
              recyclerViewBusinessList!!.adapter = listAdapter
            } else {
              showNoDataLayout("noData", getString(R.string.str_no_business))
            }

            offset += 10
          } else {
            showNoDataLayout("noData", jsonObject.optString("message"))
          }

          hideProgressBar()
        }
      // </editor-fold>

      // <editor-fold API_GET_AREA>
        StaticDataUtility.API_GET_AREA -> {

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

            val jsonArray = jsonObject.getJSONArray("areas")

            for (i in 0 until jsonArray.length()) {

              val areasObject = jsonArray.getJSONObject(i)

              val cityModel = CityModel()
              cityModel.areaId = areasObject.optString("id")
              cityModel.areaName = areasObject.optString("area")
              cityModel.areaSelected = "0"

              areaArrayList!!.add(cityModel)
            }

            areaListAdapter.updateData()

          } else {
            CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
          }

          hideProgressBar()
        }
      // </editor-fold>

      // <editor-fold API_ADD_TO_FAVORITE_BUSINESS>
        StaticDataUtility.API_ADD_TO_FAVORITE_BUSINESS -> {

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {
            setFavoriteData("1")
          } else {
            CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
          }

          hideProgressBar()
        }
      // </editor-fold>

      // <editor-fold API_REMOVE_FROM_FAVORITE_BUSINESS>
        StaticDataUtility.API_REMOVE_FROM_FAVORITE_BUSINESS -> {

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {
            setFavoriteData("0")
          } else {
            CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
          }

          hideProgressBar()
        }
      // </editor-fold>

      }
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

      if (url.equals(StaticDataUtility.API_GET_BUSINESSES, ignoreCase = true)) {
        showNoDataLayout("error", getString(R.string.str_no_business_error))
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
    recyclerViewBusinessList!!.visibility = View.GONE

    txtMessage!!.text = message
  }

  override fun onLocationGet(location: Location?) {

    showProgressBar()

    if (location != null) {
      currentLatitude = location.latitude
      currentLongitude = location.longitude
    }

    getAllBusiness("all")
  }

  override fun onBusinessClick(position: Int) {
    val intent = Intent(activity!!, BusinessDetailsActivity::class.java)
    intent.putExtra("business_id", businessArrayList!![position].businessId)
    startActivity(intent)
  }

  override fun onFavoriteClick(
    position: Int,
    business_id: String,
    type: String
  ) {
    favoritePosition = position
    addRemoveFavorite(business_id, type)
  }

  private fun setFavoriteData(favorite: String) {

    val businessModel = businessArrayList!![favoritePosition]
    businessModel.isFavorite = favorite

    businessArrayList!![favoritePosition] = businessModel

    listAdapter!!.updateData(favoritePosition)
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

  private fun setToolBar() {

    toolbar_title!!.text = String.format("%s businesses", intent.getStringExtra("category_name"))
    setSupportActionBar(main_toolbar)
  }

  private fun initUI() {

    cityArrayList = ArrayList()
    cityArrayList = CommonDataUtility.getArrayListPreference("cityList")

    Glide.with(activity!!)
        .load(R.drawable.app_banner)
        .into(imgLogo!!)

    categoryId = intent.getStringExtra("category_id")

    if (!TextUtils.isEmpty(
            MyApplication.instance.preferenceUtility
                .getString("city")
        )
    ) {
      txtCity!!.text = MyApplication.instance.preferenceUtility
          .getString("city")
    } else {
      txtCity!!.text = String.format("%s", "Surat")
    }

    distance = "1.0"
    txtDistance!!.text = String.format("%s km", distance)

    recyclerViewBusinessList!!.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    recyclerViewBusinessList!!.isNestedScrollingEnabled = false

    btnFilter!!.setOnClickListener(this)
    txtCity!!.setOnClickListener(this)
    txtDistance!!.setOnClickListener(this)

    updateLocation()

    btnRetry.setOnClickListener {
      updateLocation()
    }
  }

  private fun showCitiesDialog() {

    areaArrayList = ArrayList()

    openFilterDialog()
  }

  // <editor-fold Web Service Call>
  private fun getCities() {

    cityArrayList = ArrayList()

    showProgressBar()

    netWorkCall.makeServiceCall(activity!!, StaticDataUtility.API_GET_CITIES, this)
  }

  private fun getArea() {

    cityArrayList = ArrayList()

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["city_id"] = cityId
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(activity!!, StaticDataUtility.API_GET_AREA, this, jsonObject)
  }

  private fun getAllBusiness(isFrom: String) {

    businessArrayList = ArrayList()

    llErrorLayout!!.visibility = View.GONE
    recyclerViewBusinessList!!.visibility = View.VISIBLE

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = getUserId
      jsonObject["latitude"] = currentLatitude.toString()
      jsonObject["longitude"] = currentLongitude.toString()
      jsonObject["category_id"] = categoryId
      jsonObject["offset"] = offset.toString()

      if (isFrom == "filter") {

      }

    } catch (e: Exception) {
      e.printStackTrace()
    }

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_GET_BUSINESSES, this,
        jsonObject
    )
  }

  private fun addRemoveFavorite(
    business_id: String,
    type: String
  ) {

    showProgressBar()

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = getUserId
      jsonObject["business_id"] = business_id
    } catch (e: Exception) {
      e.printStackTrace()
    }

    if (type.equals("add", ignoreCase = true)) {
      netWorkCall.makePostServiceCall(
          activity!!, StaticDataUtility.API_ADD_TO_FAVORITE_BUSINESS,
          this, jsonObject
      )
    } else {
      netWorkCall.makePostServiceCall(
          activity!!, StaticDataUtility.API_REMOVE_FROM_FAVORITE_BUSINESS,
          this, jsonObject
      )
    }
  }
  // </editor-fold>

  private fun showList() {

    MaterialDialog.Builder(this)
        .cancelable(true)
        .title(R.string.str_select_distance)
        .items(R.array.distance)
        .itemsCallback { dialog, _, _, text ->
          dialog.dismiss()

          distance = text.toString()
          txtDistance!!.text = String.format("%s km", distance)
        }
        .show()
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  private fun hideProgressBar() {
    progressHUD.dismiss()
  }

  private fun openFilterDialog() {

    runOnUiThread {
      val dialog = Dialog(this@BusinessActivity)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.takeon_dialog_filter)
      dialog.setCancelable(true)

      val layoutParams = WindowManager.LayoutParams()
      layoutParams.copyFrom(dialog.window.attributes)
      layoutParams.width = (resources.displayMetrics.widthPixels * 0.90).toInt()
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

      dialog.window
          .setLayout(layoutParams.width, layoutParams.height)
      dialog.window
          .setBackgroundDrawable(
              ColorDrawable(Color.TRANSPARENT)
          )

      dialog.window
          .setBackgroundDrawableResource(R.drawable.roundcorner_transparent)

      val txtFilterCity = dialog.findViewById<TextView>(R.id.txtFilterCity)

      val btnFilter = dialog.findViewById<Button>(R.id.btnFilter)
      val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

      txtFilterCity.setOnClickListener { openCitiesDialog(txtFilterCity) }

      val recyclerViewArea = dialog.findViewById<RecyclerView>(R.id.recyclerViewArea)
      recyclerViewArea.layoutManager =
          GridLayoutManager(activity, 2)

      areaListAdapter = AreaListAdapter(areaArrayList!!)

      recyclerViewArea.adapter = areaListAdapter
      activity!!.onWindowFocusChanged(true)

      btnFilter.setOnClickListener {

        for (i in 0 until areaArrayList!!.size) {
          if (areaArrayList!![i].areaSelected == "1") {
            areaName = areaArrayList!![i].areaName
          }
        }

        when {
          txtFilterCity.text.toString().trim() == "" -> CommonDataUtility.showSnackBar(
              llRoot, "Please select city first"
          )
          areaName == "" -> CommonDataUtility.showSnackBar(llRoot, "Please select area name")
          else -> {

            txtCity.text = StringBuilder().append(txtFilterCity.text)
                .append("\n")
                .append("(")
                .append(areaName)
                .append(")")
            dialog.dismiss()
          }
        }
      }

      btnCancel.setOnClickListener { dialog.dismiss() }

      dialog.show()
    }
  }

  private fun openCitiesDialog(txtFilterCity: TextView) {

    runOnUiThread {
      val dialog = Dialog(this@BusinessActivity)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.takeon_dialog_city_layout)
      dialog.setCancelable(true)

      val layoutParams = WindowManager.LayoutParams()
      layoutParams.copyFrom(dialog.window.attributes)
      layoutParams.width = (resources.displayMetrics.widthPixels * 0.90).toInt()
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

      dialog.window
          .setLayout(layoutParams.width, layoutParams.height)
      dialog.window
          .setBackgroundDrawable(
              ColorDrawable(Color.TRANSPARENT)
          )

      dialog.window
          .setBackgroundDrawableResource(R.drawable.roundcorner_transparent)

      val recyclerViewCountry = dialog.findViewById<RecyclerView>(R.id.recyclerViewCountry)
      recyclerViewCountry.layoutManager =
          LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
      recyclerViewCountry.isNestedScrollingEnabled = false

      val cityListAdapter = CityListAdapter(cityArrayList!!,
          object : CityClickListener {

            override fun onCityClick(position: Int) {
              dialog.dismiss()

              txtFilterCity.text = cityArrayList!![position]
                  .cityName
              cityId = cityArrayList!![position].cityId

              getArea()
            }
          })

      recyclerViewCountry?.adapter = cityListAdapter
      activity!!.onWindowFocusChanged(true)

      dialog.show()
    }
  }
}
