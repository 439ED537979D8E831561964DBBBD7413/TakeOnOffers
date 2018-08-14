package com.takeon.offers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R
import com.takeon.offers.model.CityModel
import kotlinx.android.synthetic.main.takeon_area_list_item.view.radioButtonArea
import java.util.ArrayList

class AreaListAdapter(
  private val areaArrayList: ArrayList<CityModel>
) : RecyclerView.Adapter<AreaListAdapter.CityViewHolder>() {

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): CityViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_area_list_item, viewGroup, false)
    return CityViewHolder(v)
  }

  fun updateData() {
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(
    holder: CityViewHolder,
    position: Int
  ) {
    holder.bind(position, areaArrayList[position])
  }

  inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      position: Int,
      cityModel: CityModel
    ) {
      itemView.radioButtonArea!!.text = cityModel.areaName
      itemView.radioButtonArea.isChecked = cityModel.areaSelected != "0"

      itemView.tag = position
      itemView.setOnClickListener {
        updateSelected(it.tag as Int)
      }
    }
  }

  private fun updateSelected(position: Int) {

    val cityModel = areaArrayList[position]

    if (cityModel.areaSelected == "1") {

      val cityModelTemp = CityModel()
      cityModelTemp.areaId = cityModel.areaId
      cityModelTemp.areaName = cityModel.areaName
      cityModelTemp.areaSelected = "0"

      areaArrayList[position] = cityModelTemp
    } else {

      val cityModelTemp = CityModel()
      cityModelTemp.areaId = cityModel.areaId
      cityModelTemp.areaName = cityModel.areaName
      cityModelTemp.areaSelected = "1"

      areaArrayList[position] = cityModelTemp
    }

    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return areaArrayList.size
  }
}
