package com.takeon.offers.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.R.drawable
import com.takeon.offers.R.layout
import com.takeon.offers.R.string
import com.takeon.offers.model.BusinessModel
import com.takeon.offers.utils.StaticDataUtility
import kotlinx.android.synthetic.main.takeon_business_list_item.view.imgFavorite
import kotlinx.android.synthetic.main.takeon_business_list_item.view.imgLogo
import kotlinx.android.synthetic.main.takeon_business_list_item.view.imgVegNonVeg
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtArea
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtBusinessName
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtCuisines
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtDistance
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtOffer
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtRedeem
import kotlinx.android.synthetic.main.takeon_business_list_item.view.txtSave
import java.util.ArrayList

class BusinessListAdapter(
  private val activity: Activity,
  private val businessArrayList: ArrayList<BusinessModel>,
  private val clickListener: ClickListener?
) : RecyclerView.Adapter<BusinessListAdapter.BusinessViewHolder>() {

  interface ClickListener {
    fun onBusinessClick(position: Int)

    fun onFavoriteClick(
      position: Int,
      business_id: String,
      type: String
    )
  }

  fun updateData(position: Int) {
    notifyItemChanged(position)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): BusinessViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(layout.takeon_business_list_item, viewGroup, false)
    return BusinessViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: BusinessViewHolder,
    position: Int
  ) {
    holder.bind(position, businessArrayList[position], activity)
  }

  inner class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      position: Int,
      businessModel: BusinessModel,
      activity: Activity
    ) {

      itemView.txtBusinessName!!.text = businessModel.businessName

      when (businessModel.categoryId) {
        "1" -> {

          when (businessModel.subCategoryId) {
            "7,8", "7,8,9", "9" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.ico_male_female_svg)
            }
            "7" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.ico_male_svg)
            }
            "8" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.ico_female_svg)
            }
          }

        }
        "2" -> {

          when (businessModel.subCategoryId) {
            "1,2", "1,2,3", "3" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.ico_veg_non_veg)
            }
            "1" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.icon_veg)
            }
            "2" -> {
              itemView.imgVegNonVeg.visibility = View.VISIBLE
              itemView.imgVegNonVeg.setImageResource(R.drawable.icon_non_veg)
            }
          }
        }
      }

      if (businessModel.city.equals("", ignoreCase = true) || businessModel.city
              .equals("null", ignoreCase = true)
      ) {
        itemView.txtArea!!.text = "N/A"
      } else {
        itemView.txtArea!!.text = businessModel.city
      }

      itemView.txtOffer!!.text = String.format("%s offers", businessModel.totalOffers)

      if (businessModel.cuisines.equals("", ignoreCase = true)) {
        itemView.txtCuisines!!.setText(string.str_cuisines_not_available)
      } else {
        itemView.txtCuisines!!.text = businessModel.cuisines
      }

      if (businessModel.isFavorite.equals("0", ignoreCase = true)) {
        itemView.imgFavorite!!.setImageResource(drawable.ico_like_un_fill_svg)
      } else {
        itemView.imgFavorite!!.setImageResource(drawable.ico_like_fill_svg)
      }

      if (!businessModel.distance.equals("", ignoreCase = true) && !businessModel.distance
              .equals("null", ignoreCase = true)
      ) {
        itemView.txtDistance!!.text = String.format("%s km", businessModel.distance)
      } else {
        itemView.txtDistance!!.text = String.format("%s km", "0")
      }

      itemView.txtRedeem!!.text = String.format("%s redeemed", businessModel.totalRedeem)
      itemView.txtSave!!.text = String.format("Save Rs. %s /-", businessModel.savingAmount)

      if (!businessModel.photo.equals("", ignoreCase = true) && !businessModel.photo
              .equals("null", ignoreCase = true)
      ) {
        Glide.with(activity)
            .load(StaticDataUtility.BUSINESS_PHOTO_URL + businessModel.photo)
            .into(itemView.imgLogo!!)
      } else {
        Glide.with(activity)
            .load(drawable.app_logo)
            .into(itemView.imgLogo!!)
      }

      itemView!!.tag = position
      itemView.setOnClickListener { v ->
        clickListener?.onBusinessClick(v.tag as Int)
      }

      itemView.imgFavorite!!.tag = position
      itemView.imgFavorite!!.setOnClickListener { v ->
        clickListener?.onFavoriteClick(
            v.tag as Int,
            businessArrayList[v.tag as Int].businessId!!,
            if (businessArrayList[v.tag as Int].isFavorite.equals("0", ignoreCase = true))
              "add"
            else
              "remove"
        )
      }
    }
  }

  override fun getItemCount(): Int {
    return businessArrayList.size
  }
}
