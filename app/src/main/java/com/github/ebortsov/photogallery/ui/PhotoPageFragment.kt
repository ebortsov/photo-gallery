package com.github.ebortsov.photogallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.ebortsov.photogallery.databinding.FragmentPhotoPageBinding
import com.google.android.material.snackbar.Snackbar

class PhotoPageFragment : Fragment() {
    private val args: PhotoPageFragmentArgs by navArgs()

    private var _binding: FragmentPhotoPageBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.loadProgressIndicator.max = 100
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(args.photoPageUri.toString())
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        binding.loadProgressIndicator.isVisible = false
                    } else {
                        binding.loadProgressIndicator.isVisible = true
                        binding.loadProgressIndicator.setProgress(newProgress)
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    val contextView = binding.webView
                    if (title != null) {
                        Snackbar.make(contextView, title, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
