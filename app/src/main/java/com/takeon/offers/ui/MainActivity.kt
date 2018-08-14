package com.takeon.offers.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.takeon.offers.R
import com.takeon.offers.fragment.FavoriteFragment
import com.takeon.offers.fragment.HomeFragment
import com.takeon.offers.fragment.ProfileFragment
import com.takeon.offers.fragment.RecentFragment
import kotlinx.android.synthetic.main.takeon_activity_main.llFavorite
import kotlinx.android.synthetic.main.takeon_activity_main.llOffers
import kotlinx.android.synthetic.main.takeon_activity_main.llProfile
import kotlinx.android.synthetic.main.takeon_activity_main.llRecent
import kotlinx.android.synthetic.main.takeon_activity_main.txtFavorite
import kotlinx.android.synthetic.main.takeon_activity_main.txtOffers
import kotlinx.android.synthetic.main.takeon_activity_main.txtProfile
import kotlinx.android.synthetic.main.takeon_activity_main.txtRecent

open class MainActivity : BaseActivity(), View.OnClickListener {

  private var activity: Activity? = null
  private var tag = ""
  private var doubleBackToExitPressedOnce = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_main)


    activity = this@MainActivity

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {
      R.id.llOffers, R.id.txtOffers ->

        if (tag != "home") {
          tabSelection(true, false, false, false)
          setFragment(0)
        }
      R.id.llFavorite, R.id.txtFavorite ->

        if (tag != "favorite") {
          tabSelection(false, true, false, false)
          setFragment(1)
        }
      R.id.llRecent, R.id.txtRecent ->

        if (tag != "recent") {
          tabSelection(false, false, true, false)
          setFragment(2)
        }
      R.id.llProfile, R.id.txtProfile ->

        if (tag != "profile") {
          tabSelection(false, false, false, true)
          setFragment(3)
        }
    }
  }

  override fun onBackPressed() {

    System.gc()

    if (tag != "home") {
      if (supportFragmentManager.backStackEntryCount > 1) {

        for (i in 0 until supportFragmentManager.backStackEntryCount - 1) {
          supportFragmentManager.popBackStack()
        }

        tag = "home"
        tabSelection(true, false, false, false)
      } else {
        backPress()
      }
    } else {
      backPress()
    }
  }

  private fun initUI() {

    txtOffers!!.setOnClickListener(this)
    txtFavorite!!.setOnClickListener(this)
    txtRecent!!.setOnClickListener(this)
    txtProfile!!.setOnClickListener(this)

    llOffers!!.setOnClickListener(this)
    llFavorite!!.setOnClickListener(this)
    llRecent!!.setOnClickListener(this)
    llProfile!!.setOnClickListener(this)

    tag = "home"
    tabSelection(true, false, false, false)
    setFragment(0)
  }

  private fun tabSelection(
    tab1: Boolean,
    tab2: Boolean,
    tab3: Boolean,
    tab4: Boolean
  ) {

    txtOffers!!.isSelected = tab1
    txtFavorite!!.isSelected = tab2
    txtRecent!!.isSelected = tab3
    txtProfile!!.isSelected = tab4
  }

  private fun setFragment(position: Int) {

    var frag: Fragment? = null

    when (position) {
      0 -> {

        for (i in 0 until supportFragmentManager.backStackEntryCount) {
          supportFragmentManager.popBackStack()
        }

        frag = HomeFragment()
        tag = "home"
      }
      1 -> {
        frag = FavoriteFragment()
        tag = "favorite"
      }
      2 -> {
        frag = RecentFragment()
        tag = "recent"
      }
      3 -> {
        frag = ProfileFragment()
        tag = "profile"
      }
    }
    pushFragment(frag)
  }

  private fun backPress() {
    if (doubleBackToExitPressedOnce) {
      finish()
      return
    }

    this.doubleBackToExitPressedOnce = true
    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT)
        .show()

    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
  }

  /**
   * Method to push any fragment into given id.
   *
   * @param fragment An instance of Fragment to show into the given id.
   */

  private fun pushFragment(fragment: Fragment?) {
    if (fragment == null) {
      return
    }

    val fragmentManager = supportFragmentManager
    if (fragmentManager != null) {
      val ft = fragmentManager.beginTransaction()
      if (ft != null) {
        ft.replace(R.id.container, fragment)
            .addToBackStack(null)
        ft.commit()
      }
    }
  }
}
