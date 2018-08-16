package com.takeon.offers.ui.loginregister

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.takeon.offers.R
import com.takeon.offers.adapter.CityListAdapter
import com.takeon.offers.adapter.CityListAdapter.CityClickListener
import com.takeon.offers.model.CityAreaResponse
import com.takeon.offers.model.CityAreaResponse.CityResponseData
import com.takeon.offers.model.LoginRegisterResponse
import com.takeon.offers.ui.BaseActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_activity_register.btnSignUp
import kotlinx.android.synthetic.main.takeon_activity_register.edtEmail
import kotlinx.android.synthetic.main.takeon_activity_register.edtGender
import kotlinx.android.synthetic.main.takeon_activity_register.edtMobile
import kotlinx.android.synthetic.main.takeon_activity_register.edtPassword
import kotlinx.android.synthetic.main.takeon_activity_register.edtUserName
import kotlinx.android.synthetic.main.takeon_activity_register.llRoot
import kotlinx.android.synthetic.main.takeon_activity_register.txtBirthDate
import kotlinx.android.synthetic.main.takeon_activity_register.txtCity
import kotlinx.android.synthetic.main.takeon_activity_register.txtLogin
import org.json.JSONObject
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class RegisterActivity : BaseActivity(), View.OnClickListener, VolleyNetWorkCall.OnResponse {

  private var activity: Activity? = null
  private var dataPicker: DatePickerDialog.OnDateSetListener? = null
  private val myCalendar = Calendar.getInstance()

  private var strUserName: String? = ""
  private var strUserMobile: String? = ""
  private var strEmail: String? = ""
  private var strPassword: String? = ""
  private var strBirthDate: String? = ""
  private var strGender: String? = ""
  private var strCity: String? = ""
  private var cityArrayList: ArrayList<CityResponseData>? = null
  private var cityId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_register)


    activity = this@RegisterActivity

    initUI()
  }

  override fun onClick(v: View) {

    when (v.id) {

      R.id.txtBirthDate ->

        DatePickerDialog(
            activity!!, dataPicker, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()

      R.id.txtCity ->

        if (cityArrayList!!.size > 0) {
          openCitiesDialog()
        }

      R.id.btnSignUp -> {

        strUserName = edtUserName!!.text.toString()
            .trim { it <= ' ' }
        strUserMobile = edtMobile!!.text.toString()
            .trim { it <= ' ' }
        strEmail = edtEmail!!.text.toString()
            .trim { it <= ' ' }
        strPassword = edtPassword!!.text.toString()
            .trim { it <= ' ' }
        strBirthDate = txtBirthDate!!.text.toString()
            .trim { it <= ' ' }
        strGender = edtGender!!.text.toString()
            .trim { it <= ' ' }
        strCity = txtCity!!.text.toString()
            .trim { it <= ' ' }

        if (CommonDataUtility.checkConnection(activity!!)) {
          val message = registerValidation()

          if (message == "true") {
            registerCall()
          } else {
            CommonDataUtility.showSnackBar(llRoot, message)
          }
        } else {
          CommonDataUtility.showSnackBar(llRoot, getString(R.string.app_name))
        }
      }

      R.id.txtLogin -> finish()
    }
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

    hideProgressBar()

    try {

      when (url) {

        StaticDataUtility.API_GET_CITIES -> {

          val cityAreaResponse = GsonBuilder().create()
              .fromJson(response.toString(), CityAreaResponse::class.java)

          if (cityAreaResponse.status.equals("true", ignoreCase = true)) {

            for (cities in cityAreaResponse.cities!!) {
              cityArrayList!!.add(cities)
            }

            CommonDataUtility.setArrayListPreference(cityArrayList!!, "cityList")
          } else {
            CommonDataUtility.showSnackBar(llRoot, cityAreaResponse.message!!)
          }
        }
      }

    } catch (e: Exception) {
      e.printStackTrace()
      CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
    }
  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

    hideProgressBar()

    try {

      val loginResponseModel = GsonBuilder().create()
          .fromJson(response, LoginRegisterResponse::class.java)

      if (loginResponseModel.status.equals("true", ignoreCase = true)) {

        val intent = Intent(activity, OTPActivity::class.java)
        intent.putExtra("isFrom", "register")
        intent.putExtra("user_id", loginResponseModel.user_id)
        intent.putExtra("otp", loginResponseModel.otp)
        startActivity(intent)
        finish()
      } else {
        CommonDataUtility.showSnackBar(llRoot, loginResponseModel.message)
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

    txtLogin!!.setOnClickListener(this)
    btnSignUp!!.setOnClickListener(this)
    txtBirthDate!!.setOnClickListener(this)
    txtCity!!.setOnClickListener(this)

    showDatePicker()
    getCities()
  }

  private fun showDatePicker() {

    dataPicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
      txtBirthDate!!.text =
          String.format(Locale.getDefault(), "%d-%d-%d", year, monthOfYear + 1, dayOfMonth)
    }
  }

  private fun registerValidation(): String {

    return if (strUserName == "") {
      "Please enter valid user name"
    } else if (!CommonDataUtility.isValidMobile(strUserMobile!!)) {
      "Please enter valid mobile number"
    } else if (!CommonDataUtility.isValidMail(strEmail!!)) {
      "Please enter valid email address"
    } else if (strPassword == "") {
      "Please enter password"
    } else if (strBirthDate == "") {
      "Please select date of birth"
    } else if (strGender == "") {
      "Please enter your gender"
    } else if (strCity == "") {
      "Please enter your city"
    } else {
      "true"
    }
  }

  private fun getCities() {

    cityArrayList = ArrayList()

    showProgressBar()

    netWorkCall.makeServiceCall(activity!!, StaticDataUtility.API_GET_CITIES, this)
  }

  private fun registerCall() {

    val jsonObject = HashMap<String, String>()
    try {
      jsonObject["fullname"] = strUserName!!
      jsonObject["mobile"] = strUserMobile!!
      jsonObject["email"] = strEmail!!
      jsonObject["password"] = strPassword!!
      jsonObject["birth_date"] = strBirthDate!!
      jsonObject["gender"] = strGender!!
      jsonObject["city"] = cityId
    } catch (e: Exception) {
      e.printStackTrace()
    }

    showProgressBar()

    netWorkCall.makePostServiceCall(activity!!, StaticDataUtility.API_SIGN_UP, this, jsonObject)
  }

  private fun showProgressBar() {
    progressHUD.show()
  }

  fun hideProgressBar() {
    progressHUD.dismiss()
  }

  private fun openCitiesDialog() {

    runOnUiThread {
      val dialog = Dialog(this@RegisterActivity)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.takeon_dialog_city_layout)
      dialog.setCancelable(true)

      val layoutParams = WindowManager.LayoutParams()
      layoutParams.copyFrom(dialog.window.attributes)
      layoutParams.width = (resources.displayMetrics.widthPixels * 0.90).toInt()
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

      dialog.window
          .setLayout(layoutParams.width, layoutParams.height)
      dialog.window
          .setBackgroundDrawable(
              ColorDrawable(Color.TRANSPARENT)
          )

      dialog.window
          .setBackgroundDrawableResource(R.drawable.roundcorner_transparent)

      val recyclerViewCountry = dialog.findViewById<RecyclerView>(R.id.recyclerViewCountry)
      recyclerViewCountry.layoutManager =
          LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
      recyclerViewCountry.isNestedScrollingEnabled = false

      val cityListAdapter = CityListAdapter(cityArrayList!!,
          object : CityClickListener {

            override fun onCityClick(position: Int) {
              dialog.dismiss()

              txtCity!!.text = cityArrayList!![position].city
              cityId = cityArrayList!![position].id!!
            }
          })

      recyclerViewCountry?.adapter = cityListAdapter
      activity!!.onWindowFocusChanged(true)

      dialog.show()
    }
  }

//  inner class MyDialogFragment : DialogFragment() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//      super.onCreate(savedInstanceState)
//      setStyle(DialogFragment.STYLE_NORMAL, R.style.MyCustomTheme)
//    }
//
//    override fun onStart() {
//      super.onStart()
//      val d = dialog
//      if (d != null) {
//        val width = ViewGroup.LayoutParams.MATCH_PARENT
//        val height = ViewGroup.LayoutParams.MATCH_PARENT
//        d.window!!.setLayout(width, height)
//      }
//    }
//
//    override fun onCreateView(
//      inflater: LayoutInflater,
//      container: ViewGroup?,
//      savedInstanceState: Bundle
//    ): View? {
//
//      val root = inflater.inflate(
//          R.layout.takeon_dialog_city_layout, container,
//          false
//      )
//
//      val recyclerViewCountry = root.findViewById<RecyclerView>(R.id.recyclerViewCountry)
//      recyclerViewCountry.layoutManager =
//          LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//      recyclerViewCountry.isNestedScrollingEnabled = false
//
//      val cityListAdapter = CityListAdapter(activity, cityArrayList!!,
//          object : CityListAdapter.CityClickListener {
//
//            override fun onCityClick(position: Int) {
//              dialog.dismiss()
//
//              txtCity!!.text = cityArrayList!![position]
//                  .cityName
//              cityId = cityArrayList!![position].cityId
//            }
//          })
//
//      recyclerViewCountry.adapter = cityListAdapter
//      activity!!.onWindowFocusChanged(true)
//      return root
//    }
//  }
}
