package com.takeon.offers.ui.business

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import com.takeon.offers.R
import com.takeon.offers.adapter.FullScreenAdapter
import com.takeon.offers.model.ImageModel
import kotlinx.android.synthetic.main.takeon_activity_full_screen.imgBack
import kotlinx.android.synthetic.main.takeon_activity_full_screen.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_full_screen.txtTotalImages
import kotlinx.android.synthetic.main.takeon_activity_full_screen.viewPager

class FullScreenViewActivity : AppCompatActivity() {

  private var activity: Activity? = null
  private var position = 0

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_full_screen)
    activity = this@FullScreenViewActivity

    setToolBar()
    initUI()
  }

  private fun setToolBar() {
    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }
  }

  private fun initUI() {

    val bundle = intent.extras

    position = bundle.getInt("position", 0)
    val productImages = bundle.getSerializable("images") as ArrayList<ImageModel>

    val fullScreenAdapter = FullScreenAdapter(activity!!, productImages)
    viewPager.adapter = fullScreenAdapter
    viewPager.currentItem = position

    txtTotalImages.text = String.format("%d / %s", (position + 1), productImages.size)

    viewPager.addOnPageChangeListener(object : OnPageChangeListener {

      override fun onPageScrollStateChanged(state: Int) {
      }

      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
        txtTotalImages.text =
            String.format("%d / %s", viewPager.currentItem + 1, productImages.size)
      }

      override fun onPageSelected(position: Int) {

      }
    })
  }
}
