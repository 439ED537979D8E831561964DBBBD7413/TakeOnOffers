package com.takeon.offers.model

import java.io.Serializable
import java.util.ArrayList

class BusinessOffersResponse : Serializable {

  var status: String? = ""
  var message: String? = ""
  val business_offers: ArrayList<BusinessOffers>? = null
  val offers: ArrayList<BusinessOffers>? = null
}
