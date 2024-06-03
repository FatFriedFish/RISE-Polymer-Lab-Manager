package edu.sefs.risepolylab.ui.searchinv

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import edu.sefs.risepolylab.databinding.FragmentOnechemicalBinding
import edu.sefs.risepolylab.databinding.FragmentSearchinventoryBinding


class OneChemicalFragment: Fragment() {

    private var _binding : FragmentOnechemicalBinding? = null
    private val args : OneChemicalFragmentArgs by navArgs()
    private lateinit var webView: WebView
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOnechemicalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        webView = binding.chemDetail
        webView.webViewClient = WebViewClient()
        val url = "https://pubchem.ncbi.nlm.nih.gov/compound/"+args.cid
        webView.loadUrl(url)

        webView.settings.javaScriptEnabled = true

        webView.settings.setSupportZoom(true)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}