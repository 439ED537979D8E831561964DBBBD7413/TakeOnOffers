package com.takeon.offers.ui.loginregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.ui.MainActivity
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_login.btnLogin
import kotlinx.android.synthetic.main.takeon_activity_login.edtPassword
import kotlinx.android.synthetic.main.takeon_activity_login.edtUserId
import kotlinx.android.synthetic.main.takeon_activity_login.llRoot
import kotlinx.android.synthetic.main.takeon_activity_login.txtForgotPassword
import kotlinx.android.synthetic.main.takeon_activity_login.txtSignUp
import org.json.JSONObject
import java.util.HashMap

class LoginActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var strUserId: String? = ""
  private var strPassword: String? = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_login)


    activity = this@LoginActivity

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.txtSignUp -> startActivity(Intent(activity, RegisterActivity::class.java))
      R.id.btnLogin -> {
        //startActivity(new Intent(activity, OTPActivity.class));

        strUserId = edtUserId!!.text.toString()
            .trim()
        strPassword = edtPassword!!.text.toString()
            .trim()

        if (CommonDataUtility.checkConnection(activity!!)) {
          val message = loginValidation()

          if (message == "true") {
            loginCall()
          } else {
            CommonDataUtility.showSnackBar(llRoot, message)
          }
        } else {
          CommonDataUtility.showSnackBar(llRoot, getString(R.string.app_name))
        }
      }
      R.id.txtForgotPassword -> startActivity(Intent(activity, ForgotPasswordActivity::class.java))
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

      val jsonObject = JSONObject(response)

      if (jsonObject.optString("status").equals("true", ignoreCase = true)) {

        edtUserId!!.text.clear()
        edtPassword!!.text.clear()

        when (jsonObject.optString("is_verified")) {
          "0" -> {
            val intent = Intent(activity, OTPActivity::class.java)
            intent.putExtra("isFrom", "login")
            intent.putExtra("user_id", jsonObject.optString("user_id"))
            intent.putExtra("otp", jsonObject.optString("otp"))
            startActivity(intent)

          }
          "1" -> {
            CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))

            val resultObject = jsonObject.optJSONObject("result")

            setData(resultObject)
            startActivity(Intent(activity, MainActivity::class.java))
            finish()
          }
        }
      } else {
        CommonDataUtility.showSnackBar(llRoot, jsonObject.optString("message"))
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

    btnLogin!!.setOnClickListener(this)
    txtSignUp!!.setOnClickListener(this)
    txtForgotPassword!!.setOnClickListener(this)
  }

  private fun loginValidation(): String {

    return when {
      strUserId == "" -> "Please enter valid user name"
      strPassword == "" -> "Please enter password"
      else -> "true"
    }
  }

  private fun loginCall() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["email"] = strUserId!!
      jsonObject["password"] = strPassword!!
      jsonObject["device_token"] = MyApplication.instance.preferenceUtility.token
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(
        activity!!, StaticDataUtility.API_CUSTOMER_LOGIN, this, jsonObject
    )
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  fun hideProgressBar() {
    progressHUD.dismiss()
  }
}
