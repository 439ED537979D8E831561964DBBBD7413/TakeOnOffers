package com.takeon.offers.model

class CategoryResponse {

  var status: String? = ""
  var categories: ArrayList<CategoryModel>? = null
  var slider: ArrayList<CategoryModel>? = null

  class CategoryModel {

    var id: String? = ""
    var category: String? = ""
    var image: String? = ""
  }
}
