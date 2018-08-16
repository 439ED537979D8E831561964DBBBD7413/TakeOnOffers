package com.takeon.offers.adapter

//import kotlinx.android.synthetic.main.takeon_offers_list_item.view.llRootItem
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takeon.offers.R
import com.takeon.offers.model.BusinessOffers
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
  private val offersArrayList: ArrayList<BusinessOffers>,
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
      offersModel: BusinessOffers,
      isFrom: String
    ) {

      itemView.tag = position
      itemView.setOnClickListener { v ->
        listener.onOfferClick(v.tag as Int)
      }

      itemView.txtNumber!!.text = ((position + 1).toString())
      itemView.txtOfferName!!.text = offersModel.title
      itemView.txtOfferAmount!!.text =
          String.format("Offer Amount : %s Rs.", offersModel.amount)

      when {
        offersModel.description == "" -> itemView.txtOfferDescription.visibility = View.GONE
        else -> {
          itemView.txtOfferDescription.visibility = View.VISIBLE

          if (isFrom == "business") {
            val maxLength = 150
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(maxLength)
            itemView.txtOfferDescription.filters = fArray
          }

          itemView.txtOfferDescription.text =
              String.format("Offer Description : %s ", offersModel.description)
        }
      }

      if (isFrom == "business") {
        val daysList = getDays(offersModel.eligible_days!!)

        when {
          daysList.size == 0 -> itemView.daysView!!.visibility = View.GONE
          else -> {
            itemView.daysView!!.visibility = View.VISIBLE

            for (i in 0 until daysList.size) {

              val day = daysList[i]

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
      } else {
        itemView.daysView!!.visibility = View.GONE
      }
    }
  }

  private fun getDays(eligible_days: String): ArrayList<String> {

    val days = ArrayList<String>()

    val temp = eligible_days.split(",")

    for (aTemp in temp) {
      days.add(
          aTemp.replace("[", "").replace("]", "")
              .replace("\"", "").trim { it <= ' ' }
      )
    }

    return days
  }

  override fun getItemCount(): Int {
    return offersArrayList.size
  }
}
