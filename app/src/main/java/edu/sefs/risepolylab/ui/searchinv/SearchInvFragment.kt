package edu.sefs.risepolylab.ui.searchinv

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.databinding.FragmentSearchinventoryBinding

class SearchInvFragment : Fragment() {

    private var _binding: FragmentSearchinventoryBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchinventoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //clear boxes
        binding.searchByCAS.text.clear()
        binding.searchByCAS.hint = "Input CAS Number"

        //fetch 100 ChemicalMetaList
        viewModel.fetchAllChemicalMetaList()

        //handle RV
        val adapter = ChemMetaAdapter(viewModel){
            Log.d("SerchInvFragment", "Navigate to OneChemical CAS:" + it)
            viewModel.getChemCID(it)
            findNavController().navigate(SearchInvFragmentDirections.actionNavSearchinvToNavOnechemical(it))
        }
        val rv = binding.searchRV
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        viewModel.observeChemicalMetaList().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        //implement search
        binding.searchByCasButton.setOnClickListener {
            if (binding.searchByCAS.text.isNotEmpty()){
                val cas : String = binding.searchByCAS.text.toString()
                viewModel.filterChemicalMetaList(cas)
            }
            else{
                viewModel.fetchAllChemicalMetaList()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}