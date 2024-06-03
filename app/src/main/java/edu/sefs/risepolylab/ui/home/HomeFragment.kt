package edu.sefs.risepolylab.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import edu.sefs.risepolylab.AuthInit
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.R
import edu.sefs.risepolylab.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.updateUser()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //fill in content from viewmodel
        viewModel.observeUserName().observe(viewLifecycleOwner){
            binding.homeUsername.text = it
        }
        viewModel.observeUserStatus().observe(viewLifecycleOwner){
            binding.homeStatus.text = it
            if (it == "Authorized"){
                binding.homeStatus.setTextColor(Color.GREEN)
            }else{
                binding.homeStatus.setTextColor(Color.RED)
            }
        }
        //set up listeners
        binding.homeEditButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavHomeprofile())
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handle RV
        val adapter = TrainingMetaAdapter(viewModel)
        val rv = binding.trainingRV
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        viewModel.observeTrainingMetaList().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        //update DB
        viewModel.observeUid().observe(viewLifecycleOwner){
            if (it != "Uninitialized") {
                viewModel.fetchTrainingMetaList(resultListener = {})
                viewModel.fetchUserStatus(resultListener = {})
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}