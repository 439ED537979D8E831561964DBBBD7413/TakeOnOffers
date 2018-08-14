package com.takeon.offers.adapter

//import kotlinx.android.synthetic.main.takeon_offers_list_item.view.llRootItem
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R
import com.takeon.offers.model.OffersModel
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.daysView
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtFriday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtMonday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtNumber
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtOfferAmount
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtOfferDescription
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtOfferName
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtSaturday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtSunday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtThursday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtTuesday
import kotlinx.android.synthetic.main.takeon_offers_list_item.view.txtWednesday
import java.util.ArrayList

class BusinessOffersListAdapter(
  private val offersArrayList: ArrayList<OffersModel>,
  val listener: ClickListener,
  val isFrom: String
) : RecyclerView.Adapter<BusinessOffersListAdapter.OffersViewHolder>() {

  interface ClickListener {
    fun onOfferClick(position: Int)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): OffersViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_offers_list_item, viewGroup, false)
    return OffersViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: OffersViewHolder,
    position: Int
  ) {
    holder.bind(position, offersArrayList[position], isFrom)
  }

  inner class OffersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(
      position: Int,
      offersModel: OffersModel,
      isFrom: String
    ) {

      itemView.tag = position
      itemView.setOnClickListener { v ->
        listener.onOfferClick(v.tag as Int)
      }

      itemView.txtNumber!!.text = ((position + 1).toString())
      itemView.txtOfferName!!.text = offersModel.offerName
      itemView.txtOfferAmount!!.text =
          String.format("Offer Amount : %s Rs.", offersModel.offerAmount)

      when {
        offersModel.offerDescription == "" -> itemView.txtOfferDescription.visibility = View.GONE
        else -> {
          itemView.txtOfferDescription.visibility = View.VISIBLE

          if (isFrom == "business") {
            val maxLength = 150
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(maxLength)
            itemView.txtOfferDescription.filters = fArray
          }

          itemView.txtOfferDescription.text =
              String.format("Offer Description : %s ", offersModel.offerDescription)
        }
      }

      when {
        offersModel.offerDays.size == 0 -> itemView.daysView!!.visibility = View.GONE
        else -> {
          itemView.daysView!!.visibility = View.VISIBLE

          for (i in 0 until offersModel.offerDays.size) {

            val day = offersModel.offerDays[i]

            when (day) {
              "1" -> {
                itemView.txtMonday!!.visibility = View.VISIBLE
              }
              "2" -> {
                itemView.txtTuesday!!.visibility = View.VISIBLE
              }
              "3" -> {
                itemView.txtWednesday!!.visibility = View.VISIBLE
              }
              "4" -> {
                itemView.txtThursday!!.visibility = View.VISIBLE
              }
              "5" -> {
                itemView.txtFriday!!.visibility = View.VISIBLE
              }
              "6" -> {
                itemView.txtSaturday!!.visibility = View.VISIBLE
              }
              "7" -> {
                itemView.txtSunday!!.visibility = View.VISIBLE
              }
            }
          }
        }
      }
    }
  }

  override fun getItemCount(): Int {
    return offersArrayList.size
  }
}
