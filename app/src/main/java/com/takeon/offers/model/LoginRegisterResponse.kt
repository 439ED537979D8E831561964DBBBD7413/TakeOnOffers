package com.takeon.offers.model

class LoginRegisterResponse {

  var status: String? = ""
  var result: LoginResultModel? = null
  var is_verified: String? = ""
  var message: String? = ""
  var user_id: String? = ""
  var otp: String? = ""

  class LoginResultModel {

    var user_id: String? = ""
    var firstname: String? = ""
    var lastname: String? = ""
    var address: String? = ""
    var photo: String? = ""
    var mobile: String? = ""
    var email: String? = ""
    var city_id: String? = ""
    var city: String? = ""
  }
}
