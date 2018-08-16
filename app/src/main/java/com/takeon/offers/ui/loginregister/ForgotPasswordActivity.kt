package com.takeon.offers.ui.loginregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.model.LoginRegisterResponse
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_forgot_password.btnSubmit
import kotlinx.android.synthetic.main.takeon_activity_forgot_password.edtUserId
import kotlinx.android.synthetic.main.takeon_activity_forgot_password.llRoot
import org.json.JSONObject
import java.util.HashMap

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var strEmailMobile = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_forgot_password)

    activity = this@ForgotPasswordActivity

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.btnSubmit -> {

        strEmailMobile = edtUserId!!.text.toString()
            .trim { it <= ' ' }

        if (CommonDataUtility.checkConnection(activity!!)) {
          val message = forgotPasswordValidation()

          if (message == "true") {
            forgotPassword()
          } else {
            CommonDataUtility.showSnackBar(llRoot, message)
          }
        } else {
          CommonDataUtility.showSnackBar(llRoot, getString(R.string.app_name))
        }
      }
    }
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

    hideProgressBar()
    try {
      if (url.equals(StaticDataUtility.API_FORGET_PASSWORD, ignoreCase = true)) {

        val forgotPasswordResponseModel = GsonBuilder().create()
            .fromJson(response, LoginRegisterResponse::class.java)

        if (forgotPasswordResponseModel.status.equals("true", ignoreCase = true)) {

          val intent = Intent(activity, OTPActivity::class.java)
          intent.putExtra("isFrom", "forgot_password")
          intent.putExtra("user_id", forgotPasswordResponseModel.user_id)
          intent.putExtra("otp", forgotPasswordResponseModel.otp)
          startActivity(intent)
          finish()
        } else {
          CommonDataUtility.showSnackBar(llRoot, forgotPasswordResponseModel.message)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
    }

  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {
    hideProgressBar()
    CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
  }

  private fun initUI() {
    btnSubmit!!.setOnClickListener(this)
  }

  private fun forgotPasswordValidation(): String {

    return if (strEmailMobile == "") {
      "Please enter valid email address or mobile number"
    } else {
      "true"
    }
  }

  private fun forgotPassword() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["email"] = strEmailMobile
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_FORGET_PASSWORD, this,
        jsonObject
    )
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
