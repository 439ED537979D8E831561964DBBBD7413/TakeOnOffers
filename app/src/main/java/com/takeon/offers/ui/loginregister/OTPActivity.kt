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
import com.takeon.offers.ui.MainActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_otp.btnResend
import kotlinx.android.synthetic.main.takeon_activity_otp.btnSubmit
import kotlinx.android.synthetic.main.takeon_activity_otp.firstPinView
import kotlinx.android.synthetic.main.takeon_activity_otp.llRoot
import org.json.JSONObject
import java.util.HashMap

class OTPActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var isFrom = ""
  private var userId = ""
  private var strOTP = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_otp)

    activity = this@OTPActivity

    getIntentData()
    initUI()
    setPinView()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.btnResend -> resendOTP()

      R.id.btnSubmit -> {

        strOTP = firstPinView!!.text.toString()
            .trim { it <= ' ' }

        if (CommonDataUtility.checkConnection(activity!!)) {
          val message = otpValidation()

          if (message == "true") {
            verifyOTP()
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

      when (url) {

        StaticDataUtility.API_RESEND_SIGN_UP_OTP -> {

          val otpResponseModel = GsonBuilder().create()
              .fromJson(response, LoginRegisterResponse::class.java)

          if (otpResponseModel.status.equals("true", ignoreCase = true)) {
            strOTP = otpResponseModel.otp!!
            println("Hardik otp --> $strOTP")

            CommonDataUtility.showSnackBar(llRoot, "OTP resend successfully!!!")
          } else {
            CommonDataUtility.showSnackBar(llRoot, otpResponseModel.message)
          }

        }

        StaticDataUtility.API_CHECK_SIGN_UP_OTP -> {

          val otpResponseModel = GsonBuilder().create()
              .fromJson(response, LoginRegisterResponse::class.java)

          if (otpResponseModel.status.equals("true", ignoreCase = true)) {

            setData(otpResponseModel.result!!)
            startActivity(Intent(activity, MainActivity::class.java))
            finish()
          } else {
            CommonDataUtility.showSnackBar(llRoot, otpResponseModel.message)
          }

        }

        StaticDataUtility.API_CHECK_FORGET_OTP -> {

          val otpResponseModel = GsonBuilder().create()
              .fromJson(response, LoginRegisterResponse::class.java)

          if (otpResponseModel.status.equals("true", ignoreCase = true)) {

            val intent = Intent(activity, ResetPasswordActivity::class.java)
            intent.putExtra("isFrom", isFrom)
            intent.putExtra("user_id", otpResponseModel.user_id)
            startActivity(intent)
            finish()
          } else {
            CommonDataUtility.showSnackBar(llRoot, otpResponseModel.message)
          }

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

  private fun getIntentData() {

    isFrom = intent.getStringExtra("isFrom")
    userId = intent.getStringExtra("user_id")
    val otp = intent.getStringExtra("otp")

    println("Hardik otp --> $otp")
  }

  private fun initUI() {
    btnSubmit!!.setOnClickListener(this)
    btnResend!!.setOnClickListener(this)
  }

  private fun setPinView() {
    firstPinView!!.setAnimationEnable(true)
  }

  private fun otpValidation(): String {

    return when {
      strOTP == "" -> "Please enter otp"
      strOTP.length < 4 -> "OTP should be 5 digit length"
      else -> "true"
    }
  }

  private fun resendOTP() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = userId
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_RESEND_SIGN_UP_OTP, this,
        jsonObject
    )
  }

  private fun verifyOTP() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = userId
      jsonObject["otp"] = strOTP
      jsonObject["device_token"] = MyApplication.instance.preferenceUtility.token
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    when {
      isFrom.equals("register", ignoreCase = true) -> netWorkCall.makePostServiceCall(
          activity!!, StaticDataUtility.API_CHECK_SIGN_UP_OTP, this,
          jsonObject
      )
      isFrom.equals("forgot_password", ignoreCase = true) -> netWorkCall.makePostServiceCall(
          activity!!, StaticDataUtility.API_CHECK_FORGET_OTP, this,
          jsonObject
      )
      isFrom.equals("login", ignoreCase = true) -> netWorkCall.makePostServiceCall(
          activity!!, StaticDataUtility.API_CHECK_SIGN_UP_OTP, this,
          jsonObject
      )
    }
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
