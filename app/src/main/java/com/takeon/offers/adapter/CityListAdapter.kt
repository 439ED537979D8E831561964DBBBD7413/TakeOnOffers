package com.takeon.offers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R
import com.takeon.offers.model.CityModel
import kotlinx.android.synthetic.main.takeon_city_list_item.view.txtCity
import java.util.ArrayList

class CityListAdapter(
  private val cityArrayList: ArrayList<CityModel>,
  private val clickListener: CityClickListener?
) : RecyclerView.Adapter<CityListAdapter.CityViewHolder>() {

  interface CityClickListener {
    fun onCityClick(position: Int)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): CityViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_city_list_item, viewGroup, false)
    return CityViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: CityViewHolder,
    position: Int
  ) {
    holder.bind(cityArrayList[position])
  }

  inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(cityModel: CityModel) {
      itemView.txtCity!!.text = cityModel.cityName

      itemView.setOnClickListener {
        clickListener?.onCityClick(adapterPosition)
      }
    }
  }

  override fun getItemCount(): Int {
    return cityArrayList.size
  }
}
