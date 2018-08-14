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

//    imageList.add("https://i.pinimg.com/originals/c6/a2/89/c6a289648d689ebf779f5c11b2699ab5.jpg")
//    imageList.add("https://www.planwallpaper.com/static/images/Old_tracks_iPhone_6_Wallpapers.jpg")
//    imageList.add(
//        "http://papers.co/wallpaper/papers.co-aa99-wallpaper-europe-and-africa-worldmap-33-iphone6-wallpaper.jpg"
//    )
//    imageList.add(
//        "https://images.designtrends.com/wp-content/uploads/2016/03/29071910/Very-nice-Fireworks-iPhone-6-Wallpaper.jpg"
//    )

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
