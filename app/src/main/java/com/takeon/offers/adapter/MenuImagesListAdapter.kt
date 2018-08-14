package com.takeon.offers.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.takeon.offers.R
import com.takeon.offers.model.ImageModel
import kotlinx.android.synthetic.main.takeon_images_list_item.view.imgMenuImage
import java.util.ArrayList

class MenuImagesListAdapter(
  private val activity: Activity,
  private val imagesList: ArrayList<ImageModel>,
  private val clickListener: MenuImageClickListener?
) : RecyclerView.Adapter<MenuImagesListAdapter.ImageViewHolder>() {

  interface MenuImageClickListener {
    fun onMenuImageClick(position: Int)
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): ImageViewHolder {
    val v = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.takeon_images_list_item, viewGroup, false)
    return ImageViewHolder(v)
  }

  override fun onBindViewHolder(
    holder: ImageViewHolder,
    position: Int
  ) {
    holder.bind(imagesList[position])
  }

  inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(imageModel: ImageModel) {

      Glide.with(activity)
          .load(imageModel.pathName)
          .into(itemView.imgMenuImage!!)

      itemView.setOnClickListener {
        clickListener?.onMenuImageClick(adapterPosition)
      }
    }
  }

  override fun getItemCount(): Int {
    return imagesList.size
  }
}
