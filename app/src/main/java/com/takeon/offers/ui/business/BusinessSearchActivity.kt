package com.takeon.offers.ui.business

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.adapter.BusinessSearchListAdapter
import com.takeon.offers.model.BusinessResponse
import com.takeon.offers.model.SingleBusinessData
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_business_search.btnSearch
import kotlinx.android.synthetic.main.takeon_activity_business_search.edtBusinessSearch
import kotlinx.android.synthetic.main.takeon_activity_business_search.imgBack
import kotlinx.android.synthetic.main.takeon_activity_business_search.llRoot
import kotlinx.android.synthetic.main.takeon_activity_business_search.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_business_search.recyclerViewBusinessSearch
import kotlinx.android.synthetic.main.takeon_layout_error.btnRetry
import kotlinx.android.synthetic.main.takeon_layout_error.llErrorLayout
import kotlinx.android.synthetic.main.takeon_layout_error.txtMessage
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BusinessSearchActivity : BaseActivity(),
    BusinessSearchListAdapter.ClickListener,
    View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var businessArrayList: ArrayList<SingleBusinessData>? = null
  private var listAdapter: BusinessSearchListAdapter? = null
  private var businessName = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_business_search)

    activity = this@BusinessSearchActivity

    setToolBar()
    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {
      R.id.btnSearch -> {
        setClick()
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
      // <editor-fold API_BUSINESS_SEARCH>
        StaticDataUtility.API_BUSINESS_SEARCH -> {

          hideProgressBar()

          val businessResponseModel = GsonBuilder().create()
              .fromJson(response, BusinessResponse::class.java)

          if (businessResponseModel.status.equals("true", ignoreCase = true)) {

            for (business in businessResponseModel.businesses!!) {
              businessArrayList!!.add(business)
            }

            if (businessArrayList!!.size > 0) {

              llErrorLayout!!.visibility = View.GONE
              recyclerViewBusinessSearch!!.visibility = View.VISIBLE

              listAdapter = BusinessSearchListAdapter(businessArrayList!!, this)
              recyclerViewBusinessSearch!!.adapter = listAdapter
            } else {
              showNoDataLayout("noData", getString(R.string.str_no_business))
            }

          } else {
            showNoDataLayout("noData", businessResponseModel.message!!)
          }
        }
      // </editor-fold>
      }

    } catch (e: Exception) {
      e.printStackTrace()
      hideProgressBar()
    }
  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {

    hideProgressBar()
    showNoDataLayout("error", getString(R.string.str_no_business_error))
  }

  private fun showNoDataLayout(
    type: String,
    message: String
  ) {

    btnRetry.visibility = if (type == "error") View.GONE else View.VISIBLE

    llErrorLayout!!.visibility = View.VISIBLE
    recyclerViewBusinessSearch!!.visibility = View.GONE

    txtMessage!!.text = message
  }

  override fun onBusinessClick(position: Int) {
    val intent = Intent(activity!!, BusinessDetailsActivity::class.java)
    intent.putExtra("business_id", businessArrayList!![position].id)
    startActivity(intent)
  }

  private fun setToolBar() {

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }
  }

  private fun initUI() {

    btnSearch.setOnClickListener(this)

    recyclerViewBusinessSearch.layoutManager =
        LinearLayoutManager(this@BusinessSearchActivity, LinearLayoutManager.VERTICAL, false)
    recyclerViewBusinessSearch.isNestedScrollingEnabled = false

    edtBusinessSearch.setOnEditorActionListener { _, actionId, event ->
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        setClick()
        true
      } else {
        false
      }
    }
  }

  private fun setClick() {

    businessName = edtBusinessSearch.text.toString()
        .trim()

    if (businessName == "") {
      CommonDataUtility.showSnackBar(llRoot, "Please enter business name for search!!!")
    } else {

      CommonDataUtility.hideKeyboardFrom(this@BusinessSearchActivity, edtBusinessSearch)
      getSearchBusiness()
    }
  }

  private fun getSearchBusiness() {

    businessArrayList = ArrayList()

    val jsonObject = HashMap<String, String>()
    try {

      jsonObject["business_name_string"] = businessName
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_BUSINESS_SEARCH, this,
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
