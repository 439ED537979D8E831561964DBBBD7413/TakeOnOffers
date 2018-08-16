package com.takeon.offers.ui.profile

import android.os.Bundle
import com.takeon.offers.R
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import kotlinx.android.synthetic.main.takeon_activity_about_us.btnAboutMore
import kotlinx.android.synthetic.main.takeon_activity_about_us.imgBack
import kotlinx.android.synthetic.main.takeon_activity_about_us.main_toolbar

/**
 * Created by admin on 15/02/18.
 */

class AboutUsActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_about_us)

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }

    btnAboutMore.setOnClickListener {
      CommonDataUtility.openWebSite(this@AboutUsActivity,"http://www.takeongroup.com/")
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }
}