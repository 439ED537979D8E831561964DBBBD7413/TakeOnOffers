package com.takeon.offers.model

import java.io.Serializable
import java.util.ArrayList

class OffersModel : Serializable {

  var offerId: String = ""
  var offerName: String = ""
  var offerAmount: String = ""
  var offerDescription: String = ""
  var offerDays = ArrayList<String>()
}
