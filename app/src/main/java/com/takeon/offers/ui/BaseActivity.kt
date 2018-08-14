package com.takeon.offers.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.takeon.offers.kprogresshud.KProgressHUD
import com.takeon.offers.utils.VolleyNetWorkCall
import org.json.JSONObject

open class BaseActivity : AppCompatActivity() {

  internal lateinit var netWorkCall: VolleyNetWorkCall
  internal lateinit var progressHUD: KProgressHUD

  val getUserId: String
    get() = MyApplication.instance.preferenceUtility.userId

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    netWorkCall = VolleyNetWorkCall()

    progressHUD = KProgressHUD.create(this)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setCancellable(false)

    getToken()
  }

  fun setData(resultObject: JSONObject) {

    MyApplication.instance.preferenceUtility.login = true
    MyApplication.instance
        .preferenceUtility.userId = resultObject.optString("user_id")
    MyApplication.instance.preferenceUtility.firstName = resultObject.optString("firstname")
    MyApplication.instance
        .preferenceUtility.lastName = resultObject.optString("lastname")
    MyApplication.instance.preferenceUtility.mobileNumber = resultObject.optString("mobile")
    MyApplication.instance.preferenceUtility.email = resultObject.optString("email")
    MyApplication.instance.preferenceUtility.photo = resultObject.optString("photo")
    MyApplication.instance.preferenceUtility
        .setString("city", resultObject.optString("city"))
    MyApplication.instance.preferenceUtility
        .setString("city_id", resultObject.optString("city_id"))
  }

  private fun getToken() {

    // Get token
    if (MyApplication.instance.preferenceUtility.token == "") {
      FirebaseInstanceId.getInstance()
          .instanceId
          .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
              println("getInstanceId failed --> " + task.exception)
              return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result.token
            MyApplication.instance.preferenceUtility.token = token

            println("getInstanceId getToken --> $token")

          })
    }
  }
}