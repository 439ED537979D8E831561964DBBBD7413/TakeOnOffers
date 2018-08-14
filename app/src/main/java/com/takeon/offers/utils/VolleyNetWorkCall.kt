package com.takeon.offers.utils

import android.app.Activity
import android.util.Base64
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by admin on 27/01/18.
 */

class VolleyNetWorkCall {

  private var onResponse: OnResponse? = null

  interface OnResponse {
    fun onSuccessCall(
      response: JSONObject,
      url: String
    )

    fun onPostSuccessCall(
      response: String,
      url: String
    )

    fun onFailCall(
      error: VolleyError,
      url: String
    )
  }

  fun makeServiceCall(
    activity: Activity,
    url: String,
    onResponse: OnResponse?
  ) {

    this.onResponse = onResponse

    val jsonObjReq = object : JsonObjectRequest(Request.Method.GET,
        StaticDataUtility.SERVER_URL + url, null, Response.Listener { response ->
      onResponse?.onSuccessCall(response, url)
    }, Response.ErrorListener { error ->
      onResponse?.onFailCall(error, url)
    }) {
      @Throws(AuthFailureError::class)
      override fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/x-www-form-urlencoded"
        headers["Authorization"] = "Basic " +
            Base64.encodeToString("admin:1234".toByteArray(), Base64.NO_WRAP)
        return headers
      }
    }
    jsonObjReq.retryPolicy = DefaultRetryPolicy(
        60000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    // Adding request to request queue
    Volley.newRequestQueue(activity)
        .add(jsonObjReq)
  }

  fun makePostServiceCall(
    activity: Activity,
    url: String,
    onResponse: OnResponse?,
    params: Map<String, String>
  ) {

    this.onResponse = onResponse

    val stringRequest =
      object : StringRequest(Request.Method.POST, StaticDataUtility.SERVER_URL + url,
          Response.Listener { response ->
            onResponse?.onPostSuccessCall(response, url)
          }, Response.ErrorListener { error ->
        onResponse?.onFailCall(error, url)
      }) {

        override fun getBodyContentType(): String {
          return "application/x-www-form-urlencoded; charset=UTF-8"
        }

        override fun getParams(): Map<String, String> {
          return params
        }

        @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String> {
          val headers = HashMap<String, String>()
          headers["Content-Type"] = "application/x-www-form-urlencoded"
          headers["Authorization"] = "Basic " +
              Base64.encodeToString("admin:1234".toByteArray(), Base64.NO_WRAP)
          return headers
        }
      }

    stringRequest.retryPolicy = DefaultRetryPolicy(
        60000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    // Adding request to request queue
    Volley.newRequestQueue(activity)
        .add(stringRequest)
  }
}
