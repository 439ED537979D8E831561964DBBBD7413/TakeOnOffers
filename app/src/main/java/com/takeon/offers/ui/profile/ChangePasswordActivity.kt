package com.takeon.offers.ui.profile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.model.CommonApiResponse
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_change_password.btnSubmit
import kotlinx.android.synthetic.main.takeon_activity_change_password.edtConfirmPassword
import kotlinx.android.synthetic.main.takeon_activity_change_password.edtNewPassword
import kotlinx.android.synthetic.main.takeon_activity_change_password.edtOldPassword
import kotlinx.android.synthetic.main.takeon_activity_change_password.llRoot
import kotlinx.android.synthetic.main.takeon_activity_change_password.main_toolbar
import kotlinx.android.synthetic.main.takeon_activity_change_password.toolbar_title
import org.json.JSONObject
import java.util.HashMap

class ChangePasswordActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var strOldPassword = ""
  private var strPassword = ""
  private var strConfirmPassword = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_change_password)


    activity = this@ChangePasswordActivity

    setToolBar()
    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.btnSubmit -> {

        strOldPassword = edtOldPassword!!.text.toString()
            .trim { it <= ' ' }
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
      if (url.equals(StaticDataUtility.API_CHANGE_PASSWORD, ignoreCase = true)) {

        val commonApiResponse = GsonBuilder().create()
            .fromJson(response, CommonApiResponse::class.java)

        if (commonApiResponse.status.equals("true", ignoreCase = true)) {
          Toast.makeText(activity, commonApiResponse.message, Toast.LENGTH_SHORT)
              .show()
          finish()
        } else {
          CommonDataUtility.showSnackBar(llRoot, commonApiResponse.message)
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

  private fun setToolBar() {

    toolbar_title!!.text = getString(R.string.str_change_password)
    setSupportActionBar(main_toolbar)

    main_toolbar.setNavigationIcon(R.drawable.ico_arrow_back_svg)
    main_toolbar.setNavigationOnClickListener { finish() }
  }

  private fun initUI() {
    btnSubmit!!.setOnClickListener(this)
  }

  private fun resetPasswordValidation(): String {

    return when {
      strOldPassword == "" -> "Please enter old password"
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
      jsonObject["user_id"] = getUserId
      jsonObject["old_password"] = strOldPassword
      jsonObject["new_password"] = strPassword
      jsonObject["confirm_password"] = strConfirmPassword
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_CHANGE_PASSWORD, this,
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
