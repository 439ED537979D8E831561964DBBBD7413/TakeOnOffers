package com.takeon.offers.utils

/**
 * Created by admin on 25/01/18.
 */

object StaticDataUtility {

  var SERVER_URL = "http://purvik.me/takeongroup/api/customer/"
  var CATEGORY_PHOTO_URL = "http://purvik.me/takeongroup/assets/uploads/category_images/"
  var BUSINESS_PHOTO_URL = "http://purvik.me/takeongroup/assets/uploads/business_photos/"

  /* Login/Register API*/
  var API_CUSTOMER_LOGIN = "customer_login"
  var API_SIGN_UP = "signup"
  var API_RESEND_SIGN_UP_OTP = "resend_signup_otp"
  var API_CHECK_SIGN_UP_OTP = "check_signup_otp"
  var API_FORGET_PASSWORD = "forget_password"
  var API_CHECK_FORGET_OTP = "check_forget_otp"
  var API_RESET_PASSWORD = "reset_password"
  var API_CHANGE_PASSWORD = "change_password"
  var API_GET_CITIES = "get_cities"

  /* Dash Board API */
  var API_CATEGORIES = "categories"
  var API_CATEGORY_SLIDER = "category_slider"

  /* Business API */
  var API_GET_BUSINESSES = "get_businesses"
  var API_BUSINESSES = "business"
  var API_GET_AREA = "get_area"
  var API_BUSINESS_SEARCH = "business_search"
  var API_GET_FAVORITE_BUSINESSES = "get_favorite_businesses"
  var API_GET_RECENT_BUSINESSES = "get_recent_businesses"
  var API_ADD_TO_FAVORITE_BUSINESS = "add_to_favorite_business"
  var API_REMOVE_FROM_FAVORITE_BUSINESS = "remove_from_favorite_business"

  /* Offers API */
  var API_GET_BUSINESS_OFFERS = "get_business_offers"
  var API_GET_REDEEM_OFFER = "get_redeem_offer"
  var API_REDEEM_OFFER = "redeem_offer"
}
