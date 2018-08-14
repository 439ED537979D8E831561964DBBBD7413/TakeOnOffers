package com.takeon.offers.adapter

import android.app.Activity
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.takeon.offers.R
import java.util.ArrayList

/**
 * View pager adapter
 */
class MyViewPagerAdapter(
  private val activity: Activity,
  private val imageList: ArrayList<String>
) : PagerAdapter() {

  private val layoutInflater: LayoutInflater =
    activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  override fun instantiateItem(
    view: ViewGroup,
    position: Int
  ): Any {
    val imageLayout = layoutInflater.inflate(R.layout.takeon_sliding_images, view, false)!!
    val imageView = imageLayout
        .findViewById<ImageView>(R.id.slidingImage)

    Glide.with(activity)
        .load(imageList[position])
        .into(imageView)

    view.addView(imageLayout, 0)
    return imageLayout
  }

  override fun getCount(): Int {
    return imageList.size
  }

  override fun isViewFromObject(
    view: View,
    obj: Any
  ): Boolean {
    return view === obj
  }

  override fun destroyItem(
    container: ViewGroup,
    position: Int,
    `object`: Any
  ) {
    val view = `object` as View
    container.removeView(view)
  }
}
