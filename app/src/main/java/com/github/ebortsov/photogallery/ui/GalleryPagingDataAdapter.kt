package com.github.ebortsov.photogallery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.placeholder
import com.github.ebortsov.photogallery.R
import com.github.ebortsov.photogallery.databinding.ListItemGalleryBinding
import com.github.ebortsov.photogallery.models.GalleryItem

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(galleryItem: GalleryItem?) {
        binding.itemImageView.setImageResource(R.drawable.image_downloading)
        galleryItem?.let {
            binding.itemImageView.load(it.urls.thumb) {
                placeholder(R.drawable.image_downloading)
            }
        }
    }
}

class GalleryPagingDataAdapter(val galleryViewModel: GalleryViewModel) :
    PagingDataAdapter<GalleryItem, PhotoViewHolder>(GalleryItemComparator) {
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (position == 1) {
            // The onBindViewHolder
        }

        val photo = getItem(position)
        holder.bind(photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
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