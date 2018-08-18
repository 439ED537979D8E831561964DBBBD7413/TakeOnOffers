package com.takeon.offers.ui.business

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.adapter.MenuImagesListAdapter
import com.takeon.offers.adapter.MenuImagesListAdapter.MenuImageClickListener
import com.takeon.offers.adapter.MyViewPagerAdapter
import com.takeon.offers.model.SingleBusinessResponse
import com.takeon.offers.model.SingleBusinessResponse.BusinessImages
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_business_details.cardBusinessLocation
import kotlinx.android.synthetic.main.takeon_activity_business_details.cardBusinessMenu
import kotlinx.android.synthetic.main.takeon_activity_business_details.cardBusinessOffer
import kotlinx.android.synthetic.main.takeon_activity_business_details.imageViewMap
import kotlinx.android.synthetic.main.takeon_activity_business_details.imgBack
import kotlinx.android.synthetic.main.takeon_activity_business_details.imgBusinessLogo
import kotlinx.android.synthetic.main.takeon_activity_business_details.indicator
import kotlinx.android.synthetic.main.takeon_activity_business_details.llRoot
import kotlinx.android.synthetic.main.takeon_activity_business_details.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_business_details.recyclerViewBusinessMenu
import kotlinx.android.synthetic.main.takeon_activity_business_details.toolbar_title
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessAddress
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessArea
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessEmail
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessLocation
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessName
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessNumber
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtBusinessType
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtContactPerson
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtCuisines
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtOffers
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtShopNumber
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtShopTime
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtTotalRedeem
import kotlinx.android.synthetic.main.takeon_activity_business_details.txtTotalSave
import kotlinx.android.synthetic.main.takeon_activity_business_details.view_pager
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BusinessDetailsActivity : BaseActivity(),
    View.OnClickListener, VolleyNetWorkCall.OnResponse, MenuImageClickListener {

  private var activity: Activity? = null
  private var imageList: ArrayList<String>? = null
  private var menuImages: ArrayList<BusinessImages>? = null
  private var currentLatitude = 0.0
  private var currentLongitude = 0.0
  private var businessId = ""
  private var totalOffers = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_business_details)

    activity = this@BusinessDetailsActivity

    setToolBar()
    initUI()
  }

  override fun onClick(v: View) {
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

    try {

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
        StaticDataUtility.API_BUSINESSES -> {

          val businessResponseModel = GsonBuilder().create()
              .fromJson(response, SingleBusinessResponse::class.java)

          if (businessResponseModel.status.equals("true", ignoreCase = true)) {

            // <editor-fold Set business details>
            val businessDetails = businessResponseModel.business

            if (businessDetails != null) {
              toolbar_title.text = businessDetails.business_name
              txtBusinessName.text = businessDetails.business_name
              txtBusinessAddress.text = businessDetails.address

              txtBusinessArea.text = StringBuilder().append("Area : ")
                  .append(businessDetails.area)
                  .append(", ")
                  .append(businessDetails.city)
                  .append(", ")
                  .append(businessDetails.state)

              txtBusinessType.text = StringBuilder().append("Business Type : ")
                  .append(businessDetails.category)
              txtCuisines.text = StringBuilder().append("Cosiness : ")
                  .append(businessDetails.cuisines)
              txtTotalSave.text = StringBuilder().append("Total Saving : ")
                  .append(businessDetails.total_saving_amount)
                  .append(" Rs.")
              txtTotalRedeem.text = StringBuilder().append("Total Redeem : ")
                  .append(businessDetails.total_reedem)
                  .append(" times")

              txtShopTime.text = StringBuilder().append("Shop Time : ")
                  .append(businessDetails.from_time)
                  .append(" - ")
                  .append(businessDetails.to_time)

              txtContactPerson.text = StringBuilder().append("Contact Person : ")
                  .append(businessDetails.authorised_person)
                  .append(" (")
                  .append(businessDetails.designation)
                  .append(")")
                  .toString()
              txtBusinessNumber.text = StringBuilder().append("Business Number : ")
                  .append(businessDetails.business_number)
              txtShopNumber.text = StringBuilder().append("Shop Number : ")
                  .append(businessDetails.shop_number)

              if (businessDetails.business_email == "") {
                txtBusinessEmail.visibility = View.GONE
              } else {
                txtBusinessEmail.text = StringBuilder().append("Business Email : ")
                    .append(businessDetails.business_email)
              }

              currentLatitude = java.lang.Double.parseDouble(businessDetails.latitude)
              currentLongitude =
                  java.lang.Double.parseDouble(businessDetails.longitude)

              setMapAndLocation(
                  StringBuilder().append("Area : ")
                      .append(businessDetails.area)
                      .append(", ")
                      .append(businessDetails.city)
                      .append(", ")
                      .append(businessDetails.state).toString()
              )
            }
            // </editor-fold>

            // <editor-fold Set business logo>

            totalOffers = if (businessResponseModel.business_offers!!.size > 0) {
              businessResponseModel.business_offers!!.size.toString()
            } else {
              "0"
            }

            txtOffers.text = StringBuilder().append(totalOffers)
                .append(" offers available")
                .toString()

            // </editor-fold>

            // <editor-fold Set business logo>
            val logoArray = businessResponseModel.logo!!

            if (logoArray.size > 0) {

              Glide.with(this)
                  .load(
                      StaticDataUtility.BUSINESS_PHOTO_URL + logoArray[0].photo
                  )
                  .into(imgBusinessLogo)
            }
            // </editor-fold>

            // <editor-fold Set business slider images>
            val shopImagesArray = businessResponseModel.shop_images!!

            for (i in 0 until shopImagesArray.size) {
              imageList?.addAll(
                  setOf(
                      StaticDataUtility.BUSINESS_PHOTO_URL + shopImagesArray[i].photo
                  )
              )
            }

            setSlider()
            // </editor-fold>

            // <editor-fold Set business menu images>
            val shopMenuArray = businessResponseModel.shop_menu!!

            if (shopMenuArray.size > 0) {

              cardBusinessMenu.visibility = View.VISIBLE

              for (images in shopMenuArray) {
                menuImages?.add(images)
              }

              val imagesListAdapter = MenuImagesListAdapter(activity!!, menuImages!!, this)
              recyclerViewBusinessMenu.adapter = imagesListAdapter
            } else {
              cardBusinessMenu.visibility = View.GONE
            }
            // </editor-fold>

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
    CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))

    try {
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  override fun onMenuImageClick(position: Int) {

    val intent = Intent(activity, FullScreenViewActivity::class.java)

    val bundle = Bundle()
    bundle.putSerializable("images", menuImages)
    bundle.putInt("position", position)
    intent.putExtras(bundle)
    startActivity(intent)
  }

  private fun setMapAndLocation(location: String) {

    txtBusinessLocation.text = String.format(
        " Business Location : %s \n\nLatitude : %s Longitude :%s", location, currentLatitude,
        currentLongitude
    )

    val latLong = StringBuilder().append(currentLatitude)
        .append(",")
        .append(currentLongitude)
        .toString()

    val url = "http://maps.googleapis.com/maps/api/staticmap?center=" + latLong +
        "&zoom=15&markers=color:red|label:A|" + latLong + "&size=300x300&sensor=false"

    Glide.with(activity!!)
        .load(url)
        .into(imageViewMap)
  }

  private fun setToolBar() {

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }
  }

  private fun initUI() {

    businessId = intent.getStringExtra("business_id")

    recyclerViewBusinessMenu.layoutManager =
        LinearLayoutManager(this@BusinessDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
    recyclerViewBusinessMenu.isNestedScrollingEnabled = false

    cardBusinessOffer.setOnClickListener {

      if (totalOffers != "0") {
        val intent = Intent(activity!!, BusinessOffersActivity::class.java)
        intent.putExtra("business_id", businessId)
        intent.putExtra("isFrom", "business")
        startActivity(intent)
      } else {
        CommonDataUtility.showSnackBar(llRoot, "No business offers available!!!")
      }
    }

    cardBusinessLocation.setOnClickListener {

      val uriBegin = "geo:$currentLatitude,$currentLongitude"
      val encodedQuery = Uri.encode(
          currentLatitude.toString() + "," + currentLongitude + "(" +
              txtBusinessName.text.toString().trim() + ")"
      )
      val intent =
        Intent(android.content.Intent.ACTION_VIEW, Uri.parse("$uriBegin?q=$encodedQuery&z=15"))
      startActivity(intent)

    }

    getBusiness()
  }

  // <editor-fold Web Service Call>

  private fun getBusiness() {

    imageList = ArrayList()
    menuImages = ArrayList()

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["business_id"] = businessId
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_BUSINESSES, this,
        jsonObject
    )
  }
  // </editor-fold>

  private fun setSlider() {

    val adapter = MyViewPagerAdapter(activity!!, imageList!!)
    view_pager!!.adapter = adapter
    indicator!!.setViewPager(view_pager)
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  private fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
