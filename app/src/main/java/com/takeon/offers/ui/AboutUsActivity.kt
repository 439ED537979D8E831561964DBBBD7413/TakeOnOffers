package com.takeon.offers.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.takeon.offers.R
import kotlinx.android.synthetic.main.activity_about_us.btnAboutMore
import kotlinx.android.synthetic.main.activity_about_us.imgBack
import kotlinx.android.synthetic.main.activity_about_us.main_toolbar

/**
 * Created by admin on 15/02/18.
 */

class AboutUsActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about_us)

    setSupportActionBar(main_toolbar)
    imgBack.setOnClickListener { finish() }

    btnAboutMore.setOnClickListener {
      openWebSite()
    }
  }

  /**
   *  Used to open web site in mobile browser
   */
  private fun openWebSite() {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse("http://www.takeongroup.com/")
    startActivity(i)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }
}