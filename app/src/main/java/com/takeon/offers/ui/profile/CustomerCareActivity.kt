package com.takeon.offers.ui.profile

import android.os.Bundle
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.ui.BaseActivity
import kotlinx.android.synthetic.main.takeon_activity_cutomer_care.imgBack
import kotlinx.android.synthetic.main.takeon_activity_cutomer_care.imgBanner
import kotlinx.android.synthetic.main.takeon_activity_cutomer_care.main_toolbar

/**
 * Created by admin on 15/02/18.
 */

class CustomerCareActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_cutomer_care)

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }

    Glide.with(this@CustomerCareActivity)
        .load(R.drawable.app_banner)
        .into(imgBanner)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }
}