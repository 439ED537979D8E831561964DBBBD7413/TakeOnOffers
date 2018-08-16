package com.takeon.offers.ui.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.adapter.MyViewPagerAdapter
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.ui.loginregister.LoginActivity
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_slider.btnLogin
import kotlinx.android.synthetic.main.takeon_activity_slider.btnSkip
import kotlinx.android.synthetic.main.takeon_activity_slider.indicator
import kotlinx.android.synthetic.main.takeon_activity_slider.view_pager
import org.json.JSONObject
import java.util.ArrayList

class SliderActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private val imageList = ArrayList<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_slider)

    activity = this@SliderActivity

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.btnSkip, R.id.btnLogin -> {

        startActivity(Intent(activity, LoginActivity::class.java))
        MyApplication.instance
            .preferenceUtility.setString("slider_shown", "y")
        finish()
      }
    }
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

    hideProgressBar()

    try {

      if (response.optString("status").equals("true", ignoreCase = true)) {

        val jsonArray = response.getJSONArray("slider")

        for (i in 0 until jsonArray.length()) {
          imageList.addAll(
              setOf(
                  StaticDataUtility.CATEGORY_PHOTO_URL + jsonArray.getJSONObject(i)
                      .optString("image")
              )
          )
        }

        setSlider()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {
    hideProgressBar()
  }

  private fun initUI() {

    btnLogin!!.setOnClickListener(this)
    btnSkip!!.setOnClickListener(this)

//    setSlider()
    getSlider()
  }

  private fun getSlider() {

    showProgressBar()

    netWorkCall.makeServiceCall(activity!!, StaticDataUtility.API_CATEGORY_SLIDER, this)
  }

  private fun setSlider() {

    val adapter = MyViewPagerAdapter(activity!!, imageList)
    view_pager!!.adapter = adapter
    indicator!!.setViewPager(view_pager)
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
