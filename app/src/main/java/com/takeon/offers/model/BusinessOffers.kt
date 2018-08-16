package com.takeon.offers.model

import java.io.Serializable
import java.util.ArrayList

class BusinessOffers : Serializable {

  var id: String? = ""
  var title: String? = ""
  var amount: String? = ""
  var is_redeem: String? = ""
  var description: String? = ""
  val eligible_days: String? = null
}
