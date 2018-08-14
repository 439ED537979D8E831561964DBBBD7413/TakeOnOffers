package com.takeon.offers.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.model.CategoryModel
import com.takeon.offers.utils.StaticDataUtility
//import kotlinx.android.synthetic.main.takeon_category_list_item.view.imgCategory
//import kotlinx.android.synthetic.main.takeon_category_list_item.view.txtCategoryName
import kotlinx.android.synthetic.main.takeon_category_list_item_temp.view.imgCategory
import kotlinx.android.synthetic.main.takeon_category_list_item_temp.view.txtCategoryName
import java.util.ArrayList

class CategoryListAdapter(
  private val activity: Activity,
  private val categoryArrayList: ArrayList<CategoryModel>,
  private val clickListener: CategoryClickListener?
) : RecyclerView.Adapter<CategoryListAdapter.ImageViewHolder>() {

  interface CategoryClickListener {
    fun categoryOnclick(position: Int)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): ImageViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_category_list_item_temp, viewGroup, false)
    return ImageViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: ImageViewHolder,
    position: Int
  ) {
    holder.bind(categoryArrayList[position], activity)
  }

  inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      categoryModel: CategoryModel,
      activity: Activity
    ) {
      itemView.txtCategoryName!!.text = categoryModel.categoryName

      if (!TextUtils.isEmpty(categoryModel.categoryImage)) {

        Glide.with(activity)
            .load(StaticDataUtility.CATEGORY_PHOTO_URL + categoryModel.categoryImage)
            .into(itemView.imgCategory!!)
      }

      itemView.setOnClickListener {
        clickListener?.categoryOnclick(adapterPosition)
      }
    }
  }

  override fun getItemCount(): Int {
    return categoryArrayList.size
  }
}
