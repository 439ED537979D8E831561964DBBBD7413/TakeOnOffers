package com.takeon.offers.ui.loginregister

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_reset_password.btnSubmit
import kotlinx.android.synthetic.main.takeon_activity_reset_password.edtConfirmPassword
import kotlinx.android.synthetic.main.takeon_activity_reset_password.edtNewPassword
import kotlinx.android.synthetic.main.takeon_activity_reset_password.llRoot
import org.json.JSONObject
import java.util.HashMap

class ResetPasswordActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var userId = ""
  private var strPassword = ""
  private var strConfirmPassword = ""
  private var isFrom = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_reset_password)


    activity = this@ResetPasswordActivity

    getIntentData()
    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.btnSubmit -> {

        strPassword = edtNewPassword!!.text.toString()
            .trim { it <= ' ' }
        strConfirmPassword = edtConfirmPassword!!.text.toString()
            .trim { it <= ' ' }

        if (CommonDataUtility.checkConnection(activity!!)) {
          val message = resetPasswordValidation()

          if (message.equals("true", ignoreCase = true)) {
            resetPassword()
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
      if (url.equals(StaticDataUtility.API_RESET_PASSWORD, ignoreCase = true)) {

        val jsonObject = JSONObject(response)

        if (jsonObject.optString("status").equals("true", ignoreCase = true)) {
          Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT)
              .show()
          finish()
        } else {
          CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      CommonDataUtility.showSnackBar(llRoot,getString(R.string.error_server))
    }

  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {
    hideProgressBar()
    CommonDataUtility.showSnackBar(llRoot,getString(R.string.error_server))
  }

  private fun getIntentData() {

    isFrom = intent.getStringExtra("isFrom")
    userId = intent.getStringExtra("user_id")
  }

  private fun initUI() {
    btnSubmit!!.setOnClickListener(this)
  }

  private fun resetPasswordValidation(): String {

    return when {
      strPassword == "" -> "Please enter password"
      !strPassword.equals(
          strConfirmPassword, ignoreCase = true
      ) -> "Password and confirm password dose't match"
      else -> "true"
    }
  }

  private fun resetPassword() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["user_id"] = userId
      jsonObject["password"] = strPassword
      jsonObject["confirm_password"] = strConfirmPassword
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_RESET_PASSWORD, this,
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
