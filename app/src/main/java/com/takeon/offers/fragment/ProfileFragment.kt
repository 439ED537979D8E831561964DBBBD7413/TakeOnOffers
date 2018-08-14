package com.takeon.offers.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.R.layout
import com.takeon.offers.ui.AboutUsActivity
import com.takeon.offers.ui.ChangePasswordActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.ui.business.BusinessOffersActivity
import com.takeon.offers.ui.loginregister.LoginActivity
import com.takeon.offers.utils.CommonDataUtility
import kotlinx.android.synthetic.main.takeon_fragment_profile.imgLogo
import kotlinx.android.synthetic.main.takeon_fragment_profile.rlRoot
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtAbout
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtChangePassword
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtCustomerCare
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtDisclaimer
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtEmail
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtLogOut
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtMobile
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtPrivacy
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtProfileName
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtRateUs
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtTerms
import kotlinx.android.synthetic.main.takeon_fragment_profile.txtUsedOffer

class ProfileFragment : BaseFragment(), View.OnClickListener {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(layout.takeon_fragment_profile, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {
      R.id.txtAbout -> {
        startActivity(Intent(activity, AboutUsActivity::class.java))
      }
      R.id.txtChangePassword -> {
        startActivity(Intent(activity, ChangePasswordActivity::class.java))
      }
      R.id.txtUsedOffer -> {
        val intent = Intent(activity, BusinessOffersActivity::class.java)
        intent.putExtra("business_id", "")
        intent.putExtra("isFrom", "profile")
        startActivity(intent)
      }
      R.id.txtTerms -> CommonDataUtility.showSnackBar(
          rlRoot, getString(R.string.str_terms_condition)
      )
      R.id.txtRateUs -> CommonDataUtility.showSnackBar(rlRoot, getString(R.string.str_rate_us))
      R.id.txtCustomerCare -> CommonDataUtility.showSnackBar(
          rlRoot, getString(R.string.str_customer_care)
      )
      R.id.txtPrivacy -> CommonDataUtility.showSnackBar(
          rlRoot, getString(R.string.str_privacy_policy)
      )
      R.id.txtDisclaimer -> CommonDataUtility.showSnackBar(
          rlRoot, getString(R.string.str_disclaimer)
      )
      R.id.txtLogOut -> {

        MyApplication.instance.preferenceUtility.clearData()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity.finish() // call this to finish the current activity
      }
    }
  }

  private fun initUI() {

    txtProfileName!!.text = String.format(
        "%s %s",
        MyApplication.instance.preferenceUtility.firstName,
        MyApplication.instance.preferenceUtility.lastName
    )

    txtMobile!!.text = String.format(
        "%s",
        MyApplication.instance.preferenceUtility.mobileNumber
    )

    txtEmail!!.text = String.format(
        "%s",
        MyApplication.instance.preferenceUtility.email
    )

    if (MyApplication.instance.preferenceUtility.getString("photo") == ""
        || MyApplication.instance.preferenceUtility.getString("photo") == "0"
    ) {
      Glide.with(activity)
          .load(R.drawable.app_logo)
          .into(imgLogo!!)
    } else {
      Glide.with(activity)
          .load(
              MyApplication.instance.preferenceUtility.getString("photo")
          )
          .into(imgLogo!!)
    }

    txtAbout!!.setOnClickListener(this)
    txtChangePassword!!.setOnClickListener(this)
    txtUsedOffer!!.setOnClickListener(this)
    txtTerms!!.setOnClickListener(this)
    txtRateUs!!.setOnClickListener(this)
    txtCustomerCare!!.setOnClickListener(this)
    txtPrivacy!!.setOnClickListener(this)
    txtDisclaimer!!.setOnClickListener(this)
    txtLogOut!!.setOnClickListener(this)
  }
}
