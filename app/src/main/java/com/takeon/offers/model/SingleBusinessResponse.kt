package com.takeon.offers.model

import java.io.Serializable

class SingleBusinessResponse {

  var status: String? = ""
  var message: String? = ""
  var business: SingleBusinessData? = null

  var logo: ArrayList<BusinessImages>? = null
  var shop_menu: ArrayList<BusinessImages>? = null
  var shop_images: ArrayList<BusinessImages>? = null
  var business_offers: ArrayList<BusinessOffers>? = null

  class BusinessImages : Serializable {
    var id: String? = ""
    var photo: String? = ""
  }
}
