package com.takeon.offers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R
import com.takeon.offers.model.CityAreaResponse.AreaResponseData
import kotlinx.android.synthetic.main.takeon_area_list_item.view.radioButtonArea
import java.util.ArrayList

class AreaListAdapter(
  private val areaArrayList: ArrayList<AreaResponseData>
) : RecyclerView.Adapter<AreaListAdapter.CityViewHolder>() {

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): CityViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_area_list_item, viewGroup, false)
    return CityViewHolder(v)
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
      cityModel: AreaResponseData
    ) {
      itemView.radioButtonArea!!.text = cityModel.area
      itemView.radioButtonArea.isChecked = cityModel.areaSelected != "0"

      itemView.radioButtonArea.tag = position
      itemView.radioButtonArea.setOnClickListener {
        updateSelected(it.tag as Int)
      }
    }
  }

  private fun updateSelected(position: Int) {

    for (i in 0 until areaArrayList.size) {

      val cityModelTemp = AreaResponseData()

      if (i == position) {

        cityModelTemp.id = areaArrayList[i].id
        cityModelTemp.area = areaArrayList[i].area
        cityModelTemp.areaSelected = "1"

        areaArrayList[position] = cityModelTemp

      } else {

        cityModelTemp.id = areaArrayList[i].id
        cityModelTemp.area = areaArrayList[i].area
        cityModelTemp.areaSelected = "0"

        areaArrayList[i] = cityModelTemp
      }
    }

    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return areaArrayList.size
  }
}
