package com.takeon.offers.model

class CityAreaResponse {

  var status: String? = ""
  var message: String? = ""
  var cities: ArrayList<CityResponseData>? = null
  var areas: ArrayList<AreaResponseData>? = null

  class CityResponseData {
    var id: String? = ""
    var city: String? = ""
  }

  class AreaResponseData {
    var id: String? = ""
    var area: String? = ""
    var areaSelected: String? = "0"
  }
}
