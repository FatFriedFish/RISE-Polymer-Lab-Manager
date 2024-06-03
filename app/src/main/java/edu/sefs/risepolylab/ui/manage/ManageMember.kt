package edu.sefs.risepolylab.ui.manage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.R
import edu.sefs.risepolylab.databinding.FragmentManagememberBinding


class ManageMember : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentManagememberBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentManagememberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //crate new user
        auth = Firebase.auth
        binding.manageAddUser.setOnClickListener {
            if (binding.manageInputEmail.text.isNotEmpty() && binding.manageInputPass.text.isNotEmpty() && binding.manageInputName.text.isNotEmpty()){
                val email = binding.manageInputEmail.text.toString()
                val password = binding.manageInputPass.text.toString()
                val name = binding.manageInputName.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) {task ->
                        if (task.isSuccessful){
                            val uid = task.result.user!!.uid
                            Log.d("Manage Member:", "New User UID:" + uid)
                            //Update new user name
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }
                            task.result.user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Log.d("Manage Member", "New user name updated")
                                    }
                                }

                            //create user status profile
                            viewModel.addMember(email, uid)

                            Snackbar.make(it, "Create User Success", Snackbar.LENGTH_SHORT).show()
                        }else{
                            Snackbar.make(it, "Create User FAILED", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Snackbar.make(it, "You must fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        //edit user
        //find user by email
        binding.manageFindUser.setOnClickListener {
            if(binding.manageFindEmail.text.isNotEmpty()){
                val email = binding.manageFindEmail.text.toString()
                viewModel.searchMember(email){uid->
                    binding.manageGotUID.text = uid
                }
            }else{
                Snackbar.make(it, "You must input email for search", Snackbar.LENGTH_SHORT).show()
            }
        }

        //set training spinner
        val spinner_training : Spinner = binding.spinnerTraining
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.trainings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner_training.adapter = adapter
        }

        val spinner_status : Spinner = binding.spinnerStatus
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner_status.adapter = adapter
        }

        binding.manageSetTraining.setOnClickListener {
            val training = when(spinner_training.selectedItemPosition){
                0 -> "New User Orientation"
                1 -> "Cryogen Safety"
                2 -> "Pressure Safety"
                3 -> "General Electrical Safety"
                4 -> "Safe Handling-Nanoscale Matter"
                5 -> "Chemical Hygiene and Safety"
                6 -> "Work Safe with Compressed Gas"
                7 -> "Fire Extinguisher Safety"
                8 -> "Hazardous Waste Generator"
                else -> "Not Specified"
            }

            val status = when(spinner_status.selectedItemPosition){
                0 -> "Completed"
                1 -> "Incomplete"
                else -> "Not Specified"
            }

            viewModel.setTraining(binding.manageGotUID.text.toString(), training, status){
                Snackbar.make(it, "Set Training Successful", Snackbar.LENGTH_SHORT).show()
            }
        }

        //delete user
        binding.manageDeleteUser.setOnClickListener {
            if (binding.manageGotUID.text.isNotEmpty()){
                viewModel.deleteUser(binding.manageGotUID.text.toString()){
                    Snackbar.make(it, "Delete User Successful", Snackbar.LENGTH_SHORT).show()
                }
            }
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}