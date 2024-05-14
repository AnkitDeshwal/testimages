package com.example.test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.ImageCache
import com.example.test.ImageLoader
import com.example.test.databinding.ItemTestBinding
import com.example.test.model.MainClassItem
import com.example.test.model.Thumbnail
import java.io.File

class TestAdapter(var context: Context, private var items: ArrayList<Thumbnail>) : RecyclerView.Adapter<TestAdapter.MyViewHolder>() {
    inner class MyViewHolder(var binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val m  = items[position]
        val imageUrl = "${m.domain}/${m.basePath}/0/${m.key}"

        val cacheDir = File(context.cacheDir, "image_cache")
        val imageCache = ImageCache(cacheDir)
        val imageLoader = ImageLoader(imageCache)

        imageLoader.loadImage(imageUrl, holder.binding.imageView)
        //holder.bind(m)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    //because happen to change item on scroll
    override fun getItemViewType(position: Int): Int {
        return position
    }
}