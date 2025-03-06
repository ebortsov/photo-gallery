package com.github.ebortsov.photogallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.github.ebortsov.photogallery.databinding.PhotoGalleryFragmentBinding
import com.github.ebortsov.photogallery.interfaces.Launchable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {
    private var _binding: PhotoGalleryFragmentBinding? = null
    private val binding: PhotoGalleryFragmentBinding get() = checkNotNull(_binding)

    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotoGalleryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photoGrid.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = GalleryPagingDataAdapter()
        binding.photoGrid.adapter = adapter

        val observeGalleryPages = Launchable {
            addOnStartedCoroutine {
                photoGalleryViewModel.galleryPages.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        addOnStartedCoroutine {
            photoGalleryViewModel.isPagingSourceReady.collectLatest { isReady ->
                if (isReady) {
                    observeGalleryPages.launch()
                }
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.bottomProgressIndicator.isVisible = loadState.append == LoadState.Loading
            binding.topProgressIndicator.isVisible = loadState.prepend == LoadState.Loading
        }

        binding.searchView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text?.toString() ?: ""
                photoGalleryViewModel.setQuery(query)
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addOnStartedCoroutine(block: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }
}
