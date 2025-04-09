package com.github.ebortsov.photogallery.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.placeholder
import com.github.ebortsov.photogallery.R
import com.github.ebortsov.photogallery.databinding.ListItemGalleryBinding
import com.github.ebortsov.photogallery.models.GalleryItem

typealias onPhotoClickListener = (v: View, item: GalleryItem) -> Unit

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding,
    private val onPhotoClickListener: onPhotoClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(galleryItem: GalleryItem?) {
        binding.itemImageView.setImageResource(R.drawable.image_downloading)
        galleryItem?.let { item ->
            binding.itemImageView.load(item.urls.thumb) {
                placeholder(R.drawable.image_downloading)
            }
            binding.itemImageView.setOnClickListener { v ->
                onPhotoClickListener(v, item)
            }
        }
    }
}

class GalleryPagingDataAdapter(private val onPhotoClickListener: onPhotoClickListener) :
    PagingDataAdapter<GalleryItem, PhotoViewHolder>(GalleryItemComparator) {
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding, onPhotoClickListener)
    }
}

object GalleryItemComparator : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem == newItem
    }
}