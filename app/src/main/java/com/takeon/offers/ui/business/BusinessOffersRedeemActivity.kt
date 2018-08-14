package com.takeon.offers.ui.business

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.model.OffersModel
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.ui.MainActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.btnRedeem
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.daysView
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.imgBack
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.llRoot
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtFriday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtMonday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtOfferAmount
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtOfferDescription
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtOfferName
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtSaturday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtSunday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtThursday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtTuesday
import kotlinx.android.synthetic.main.takeon_activity_business_offers_redeem.txtWednesday
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BusinessOffersRedeemActivity : BaseActivity(),
    View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var businessId = ""
  private var offerId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_business_offers_redeem)

    activity = this@BusinessOffersRedeemActivity

    setToolBar()
    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {
      R.id.btnRedeem -> {
        startActivityForResult(Intent(activity, QrCodeScannerActivity::class.java), 102)
      }
    }
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
      // <editor-fold API_REDEEM_OFFER>
        StaticDataUtility.API_REDEEM_OFFER -> {

          hideProgressBar()

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            startActivity(intent)
            finish() // call this to finish the current activity

          } else {
            CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
          }

        }
      // </editor-fold>
      }

    } catch (e: Exception) {
      e.printStackTrace()
      hideProgressBar()
      CommonDataUtility.showSnackBar(llRoot,getString(R.string.error_server))
    }
  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {
    hideProgressBar()
    CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
  }

  private fun setToolBar() {

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }
  }

  private fun initUI() {

    btnRedeem.setOnClickListener(this)

    val bundle = intent.extras

    businessId = bundle.getString("business_id")
    val position = bundle.getInt("position", 0)
    val productImages = bundle.getSerializable("images") as ArrayList<OffersModel>

    bind(productImages[position])
  }

  private fun bind(
    offersModel: OffersModel
  ) {

    offerId = offersModel.offerId

    txtOfferName!!.text = offersModel.offerName
    txtOfferAmount!!.text =
        String.format("Offer Amount : %s Rs.", offersModel.offerAmount)

    if (offersModel.offerDescription == "") {
      txtOfferDescription.visibility = View.GONE
    } else {
      txtOfferDescription.visibility = View.VISIBLE
      txtOfferDescription.text =
          String.format("Offer Description : %s ", offersModel.offerDescription)
    }

    if (offersModel.offerDays.size == 0) {
      daysView!!.visibility = View.GONE
    } else {
      daysView!!.visibility = View.VISIBLE

      for (i in 0 until offersModel.offerDays.size) {

        val day = offersModel.offerDays[i]

        when (day) {
          "1" -> {
            txtMonday!!.visibility = View.VISIBLE
          }
          "2" -> {
            txtTuesday!!.visibility = View.VISIBLE
          }
          "3" -> {
            txtWednesday!!.visibility = View.VISIBLE
          }
          "4" -> {
            txtThursday!!.visibility = View.VISIBLE
          }
          "5" -> {
            txtFriday!!.visibility = View.VISIBLE
          }
          "6" -> {
            txtSaturday!!.visibility = View.VISIBLE
          }
          "7" -> {
            txtSunday!!.visibility = View.VISIBLE
          }
        }
      }
    }
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 102) {
      if (resultCode == RESULT_OK) {

        if (data != null)
          redeemOffer(data.getStringExtra("pin"))
      }
    }
  }

  private fun redeemOffer(pin: String) {

    val jsonObject = HashMap<String, String>()
    try {

      jsonObject["business_id"] = businessId
      jsonObject["user_id"] = getUserId
      jsonObject["offer_id"] = offerId
      jsonObject["pin"] = pin
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_REDEEM_OFFER, this,
        jsonObject
    )
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  private fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
