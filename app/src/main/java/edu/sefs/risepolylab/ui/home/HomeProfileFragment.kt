package edu.sefs.risepolylab.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.databinding.FragmentHomeprofileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class HomeProfileFragment: Fragment() {
    private var _binding: FragmentHomeprofileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeUpdateProfile.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (binding.homeNewName.text.toString().isNotBlank()){
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.homeNewName.text.toString()
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("HomeProfileFragment", "User profile updated.")
                            Snackbar.make(requireView(),"Update Name Success", Snackbar.LENGTH_SHORT).show()
                            viewModel.updateUser()
                        }
                    }
            }

            if (binding.homeOldPassword.text.isNotEmpty() && binding.homeNewPassword.text.isNotEmpty()){
                val credential = EmailAuthProvider
                    .getCredential(viewModel.observeEmail().value!!, binding.homeOldPassword.text.toString())

                user!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        Log.d("HomeProfileFragment", "User re-authenticated.")

                        val newPassword = binding.homeNewPassword.text.toString()
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("HomeProfileFragment", "User password updated.")
                                    Snackbar.make(requireView(),"Update Password Success", Snackbar.LENGTH_SHORT).show()
                                }else{
                                    Snackbar.make(requireView(),"Update Password Failed", Snackbar.LENGTH_SHORT).show()
                                }
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