package com.github.ebortsov.photogallery.ui

import android.Manifest
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.WorkManager
import com.github.ebortsov.photogallery.databinding.FragmentPhotoGalleryBinding
import com.github.ebortsov.photogallery.databinding.SearchHistoryItemBinding
import com.github.ebortsov.photogallery.features.poll.PhotoPollingDataSource
import com.github.ebortsov.photogallery.models.GalleryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryFragment"

class GalleryFragment : Fragment() {
    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding: FragmentPhotoGalleryBinding get() = checkNotNull(_binding)

    private val galleryViewModel: GalleryViewModel by viewModels()

    private val photoPollingDataSource by lazy {
        val workManager = WorkManager.getInstance(requireContext())
        PhotoPollingDataSource(workManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        galleryViewModel // access property and force the view model initialization

        binding.photoGrid.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = GalleryPagingDataAdapter { _, item ->
            // onPhotoClickListener
            findNavController().navigate(
                GalleryFragmentDirections.galleryFragmentToPhotoPageFragment(
                    item.unsplashPhotoPageUri
                )
            )
        }

        binding.photoGrid.adapter = adapter

        addOnStartedCoroutine {
            galleryViewModel.isLoading.collectLatest { isLoading ->
                if (isLoading) {
                    binding.searchView.setText(galleryViewModel.searchQuery.value)
                }
            }
        }

        addOnStartedCoroutine {
            delay(20) // dummy delay to ensure the collectLatest will not collect initial the call to API
            galleryViewModel.galleryPages.collectLatest { pagingData: PagingData<GalleryItem> ->
                adapter.submitData(pagingData)
            }
        }

        addOnStartedCoroutine {
            galleryViewModel.searchHistory.collectLatest { history ->
                populateSearchHistory(history)
            }
        }

        addOnStartedCoroutine {
            galleryViewModel.isPolling.collectLatest { isPolling ->
                updatePolling(isPolling)
            }
        }

        adapter.addLoadStateListener { loadState ->
            // If any of the loadings caused errors, then show the loading error layout
            binding.loadingErrorLayout.isVisible = (
                    loadState.append is LoadState.Error ||
                            loadState.prepend is LoadState.Error ||
                            loadState.refresh is LoadState.Error
                    )

            // set the visibility of bottom progress indicator
            binding.bottomProgressIndicator.isVisible =
                loadState.append == LoadState.Loading || loadState.refresh is LoadState.Loading

            // set visibility of top progress indicator
            binding.topProgressIndicator.isVisible = loadState.prepend is LoadState.Loading
        }

        binding.retryButton.setOnClickListener {
            adapter.retry()
        }

        binding.searchView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // User submits the search query
                val query = v.text?.toString() ?: ""
                galleryViewModel.setQuery(query) // send query to the viewModel

                // hide the keyboard
                hideKeyboard()

                // Remove focus from the search bar
                v.clearFocus()

                // Close the history (if was open)
                showRecentHistory(false)
                true
            } else {
                false
            }
        }

        binding.closeSearchHistory.setOnClickListener {
            showRecentHistory(false)
        }
        binding.showSearchHistory.setOnClickListener {
            showRecentHistory(true)
        }

        binding.pollingSwitch.setOnCheckedChangeListener { _, isChecked ->
            galleryViewModel.setIsPolling(isChecked)
        }

        // Ask the permission to send notifications
        requestPostNotificationPermission()
    }

    private fun showRecentHistory(show: Boolean) {
        binding.searchHistoryLayout.isVisible = show
        binding.galleryLayout.isVisible = !show
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    private fun populateSearchHistory(history: List<String>) {
        Log.d(TAG, "populateSearchHistory: $history")
        binding.searchHistoryLinearLayout.removeAllViews()
        for (query in history) {
            val searchItemBinding = SearchHistoryItemBinding.inflate(
                layoutInflater,
                binding.searchHistoryLinearLayout,
                true
            )
            searchItemBinding.searchHistoryItem.setOnClickListener { v ->
                galleryViewModel.setQuery((v as TextView).text.toString())
                showRecentHistory(false)
                binding.searchView.setText(query)
            }
            searchItemBinding.root.text = query
        }
    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            val notificationManager = NotificationManagerCompat.from(requireContext())
            if (!notificationManager.areNotificationsEnabled()) {
                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    private fun updatePolling(isActive: Boolean) {
        Log.d(TAG, "updatePolling")
        binding.pollingSwitch.isChecked = isActive

        if (isActive) {
            photoPollingDataSource.startPhotoPolling()
        } else {
            photoPollingDataSource.cancelPhotoPolling()
        }
    }
}
