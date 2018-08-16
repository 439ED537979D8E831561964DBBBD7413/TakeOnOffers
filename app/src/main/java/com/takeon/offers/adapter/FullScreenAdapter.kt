package com.takeon.offers.adapter

import android.app.Activity
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.model.SingleBusinessResponse.BusinessImages
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.view.TouchImageView
import java.util.ArrayList

class FullScreenAdapter// constructor
(
  private val _activity: Activity,
  private val _imagePaths: ArrayList<BusinessImages>
) : PagerAdapter() {
  private val inflater: LayoutInflater = _activity
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  override fun getCount(): Int {
    return this._imagePaths.size
  }

  override fun isViewFromObject(
    view: View,
    `object`: Any
  ): Boolean {
    return view === `object`
  }

  override fun instantiateItem(
    container: ViewGroup,
    position: Int
  ): Any {

    val imgDisplay: TouchImageView
    val viewLayout = inflater.inflate(
        R.layout.takeon_full_screen_image, container,
        false
    )

    imgDisplay = viewLayout.findViewById(R.id.imgDisplay)

    Glide.with(_activity)
        .load(StaticDataUtility.BUSINESS_PHOTO_URL + _imagePaths[position].photo)
        .into(imgDisplay)

    container.addView(viewLayout)

    return viewLayout
  }

  override fun destroyItem(
    container: ViewGroup,
    position: Int,
    `object`: Any
  ) {
    System.gc()
    container.removeView(`object` as TouchImageView)
  }
}
