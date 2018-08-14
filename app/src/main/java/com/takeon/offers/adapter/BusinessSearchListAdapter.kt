package com.takeon.offers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R.layout
import com.takeon.offers.model.BusinessModel
import kotlinx.android.synthetic.main.takeon_business_search_list_item.view.txtBusinessName
import java.util.ArrayList

class BusinessSearchListAdapter(
  private val businessArrayList: ArrayList<BusinessModel>,
  private val clickListener: ClickListener?
) : RecyclerView.Adapter<BusinessSearchListAdapter.BusinessViewHolder>() {

  interface ClickListener {
    fun onBusinessClick(position: Int)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): BusinessViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(layout.takeon_business_search_list_item, viewGroup, false)
    return BusinessViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: BusinessViewHolder,
    position: Int
  ) {
    holder.bind(position, businessArrayList[position])
  }

  inner class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      position: Int,
      businessModel: BusinessModel
    ) {

      itemView.txtBusinessName!!.text = businessModel.businessName

      itemView.tag = position
      itemView.setOnClickListener { v ->
        clickListener?.onBusinessClick(v.tag as Int)
      }
    }
  }

  override fun getItemCount(): Int {
    return businessArrayList.size
  }
}
