package com.pro.devgatedemo.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pro.devgatedemo.databinding.PhotoRowItemBinding
import com.pro.devgatedemo.models.Image

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {
    var listener: OnClickListener? = null
    lateinit var binding: PhotoRowItemBinding
    var imageList = mutableListOf<Image>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = PhotoRowItemBinding.inflate(LayoutInflater.from(parent.context))
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i("testingTag", "onBindViewHolder: ${imageList[position].path}")
        Glide.with(holder.itemView.context).load(imageList[position].path).into(binding.imageThumb)
        holder.itemView.setOnClickListener {
            listener?.onClickListener(imageList[position])
        }
    }

    fun setData(images: MutableList<Image>) {
        this.imageList.clear()
        this.imageList.addAll(images)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    interface OnClickListener {
        fun onClickListener(image: Image)
    }
}