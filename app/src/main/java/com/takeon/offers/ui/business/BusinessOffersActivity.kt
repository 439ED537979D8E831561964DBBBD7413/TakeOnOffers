package com.takeon.offers.ui.business

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.adapter.BusinessOffersListAdapter
import com.takeon.offers.adapter.BusinessOffersListAdapter.ClickListener
import com.takeon.offers.model.OffersModel
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_business_details.imgBack
import kotlinx.android.synthetic.main.takeon_activity_business_offers.llRoot
import kotlinx.android.synthetic.main.takeon_activity_business_offers.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_business_offers.recyclerViewBusinessOffers
import kotlinx.android.synthetic.main.takeon_activity_business_offers.toolbar_title
import kotlinx.android.synthetic.main.takeon_layout_error.btnRetry
import kotlinx.android.synthetic.main.takeon_layout_error.llErrorLayout
import kotlinx.android.synthetic.main.takeon_layout_error.txtMessage
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BusinessOffersActivity : BaseActivity(),
    View.OnClickListener, VolleyNetWorkCall.OnResponse, ClickListener {

  private var activity: Activity? = null
  private var offersList: ArrayList<OffersModel>? = null
  private var businessId = ""
  private var isFrom = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_business_offers)

    activity = this@BusinessOffersActivity

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

      // <editor-fold API_GET_BUSINESS_OFFERS>
        StaticDataUtility.API_GET_BUSINESS_OFFERS -> {

          hideProgressBar()

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

            // <editor-fold Set business details>
            val businessOffers = jsonObject.optJSONArray("business_offers")

            if (businessOffers.length() > 0) {

              for (i in 0 until businessOffers.length()) {

                val offersObject = businessOffers.getJSONObject(i)

                val offersModel = OffersModel()
                offersModel.offerId = offersObject.optString("id")
                offersModel.offerName = offersObject.optString("title")
                offersModel.offerAmount = offersObject.optString("amount")
                offersModel.offerDescription = offersObject.optString("description")

                val days = ArrayList<String>()

                if (offersObject.optString("eligible_days") != "") {

                  val temp = offersObject.optString("eligible_days")
                      .split(",")

                  for (aTemp in temp) {
                    days.add(
                        aTemp.replace("[", "").replace("]", "")
                            .replace("\"", "").trim { it <= ' ' }
                    )
                  }
                }

                offersModel.offerDays = days
                offersList!!.add(offersModel)
              }
            }

            if (offersList!!.size == 0) {
              showNoDataLayout("noData", getString(R.string.str_no_business_offers))
            } else {
              val offersListAdapter = BusinessOffersListAdapter(offersList!!, this, isFrom)
              recyclerViewBusinessOffers.adapter = offersListAdapter
            }

            // </editor-fold>

          } else {
            showNoDataLayout("noData", jsonObject.optString("message"))
          }
        }
      // </editor-fold>

      // <editor-fold API_GET_REDEEM_OFFER>
        StaticDataUtility.API_GET_REDEEM_OFFER -> {

          hideProgressBar()

          val jsonObject = JSONObject(response)

          if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

            // <editor-fold Set business details>
            val redeemOffers = jsonObject.optJSONArray("offers")

            if (redeemOffers.length() > 0) {

              for (i in 0 until redeemOffers.length()) {

                val offersObject = redeemOffers.getJSONObject(i)

                val offersModel = OffersModel()
                offersModel.offerId = offersObject.optString("id")
                offersModel.offerName = offersObject.optString("title")
                offersModel.offerAmount = offersObject.optString("amount")
                offersModel.offerDescription = offersObject.optString("description")

                offersList!!.add(offersModel)
              }

              if (offersList!!.size == 0) {
                showNoDataLayout("noData", getString(R.string.str_no_redeem_offers))
              } else {
                val offersListAdapter = BusinessOffersListAdapter(offersList!!, this, isFrom)
                recyclerViewBusinessOffers.adapter = offersListAdapter
              }
            }
            // </editor-fold>

          } else {
            showNoDataLayout("noData", jsonObject.optString("message"))
          }
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

      when {
        url.equals(
            StaticDataUtility.API_GET_BUSINESS_OFFERS, ignoreCase = true
        ) -> {
          showNoDataLayout("error", getString(R.string.str_no_business_offers_error))
        }
        url.equals(StaticDataUtility.API_GET_REDEEM_OFFER, ignoreCase = true) -> {
          showNoDataLayout("error", getString(R.string.str_no_redeem_offers_error))
        }
        else -> {
          showNoDataLayout("error", getString(R.string.error_server))
        }
      }

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
    recyclerViewBusinessOffers!!.visibility = View.GONE

    txtMessage!!.text = message
  }

  override fun onOfferClick(position: Int) {

    if (isFrom == "business") {

      val intent = Intent(activity, BusinessOffersRedeemActivity::class.java)
      val bundle = Bundle()
      bundle.putSerializable("images", offersList)
      bundle.putInt("position", position)
      bundle.putString("business_id", businessId)
      intent.putExtras(bundle)
      startActivity(intent)
    }
  }

  private fun setToolBar() {

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }
  }

  private fun initUI() {

    businessId = intent.getStringExtra("business_id")
    isFrom = intent.getStringExtra("isFrom")

    when (isFrom) {
      "business" -> toolbar_title.text = getString(R.string.text_offer)
      "profile" -> toolbar_title.text = getString(R.string.str_used_offers)
    }

    recyclerViewBusinessOffers.layoutManager =
        LinearLayoutManager(this@BusinessOffersActivity, LinearLayoutManager.VERTICAL, false)
    recyclerViewBusinessOffers.isNestedScrollingEnabled = false

    getBusinessOffers()

    btnRetry.setOnClickListener { getBusinessOffers() }
  }

  // <editor-fold Web Service Call>

  private fun getBusinessOffers() {

    offersList = ArrayList()

    val jsonObject = HashMap<String, String>()
    try {
      when (isFrom) {
        "business" -> jsonObject["business_id"] = businessId
        "profile" -> jsonObject["user_id"] = getUserId
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    when (isFrom) {
      "business" -> {
        netWorkCall.makePostServiceCall(
            activity!!, StaticDataUtility.API_GET_BUSINESS_OFFERS, this,
            jsonObject
        )
      }

      "profile" -> {
        netWorkCall.makePostServiceCall(
            activity!!, StaticDataUtility.API_GET_REDEEM_OFFER, this,
            jsonObject
        )
      }
    }
  }
  // </editor-fold>

  private fun showProgressBar() {
    progressHUD.show()
  }

  private fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
