package com.example.pharmacy_store


import SharedPrefManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

class Security : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_security, container, false)

        view.setOnTouchListener { _, _ ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            false
        }

        mAuth = FirebaseAuth.getInstance()

        currentPasswordEditText = view.findViewById(R.id.CurrentPassword)
        newPasswordEditText = view.findViewById(R.id.Password)
        confirmPasswordEditText = view.findViewById(R.id.ConfirmPassword)
        val changePasswordButton = view.findViewById<Button>(R.id.button)


        changePasswordButton.setOnClickListener {
            changePassword()
        }

        val floatingActionButton: FloatingActionButton =
            view.findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            showDeleteAccountDialog()
        }

        return view
    }

    private fun changePassword() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (newPassword.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = mAuth.currentUser
        val sharedPrefManager = SharedPrefManager(requireContext())
        val email = user?.email

        if (email != null && user != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            // Update password in Firestore
                            val userId = sharedPrefManager.getUserId()
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users")
                                .document(userId)
                                .update("password", newPassword)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()

                                    currentPasswordEditText.setText("")
                                    newPasswordEditText.setText("")
                                    confirmPasswordEditText.setText("")
                                }
                                .addOnFailureListener { e ->
                                    if (e is FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(requireContext(), "Incorrect current password", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Incorrect current password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to authenticate current password", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(requireContext())

        // Inflate the custom layout
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_account_dialog, null)

        builder.setView(dialogView)

        val confirmationCheckbox = dialogView.findViewById<CheckBox>(R.id.confirmation_checkbox)

        builder.setPositiveButton("Yes") { _, _ ->
            // Retrieve user ID from SharedPrefManager
            val sharedPrefManager = SharedPrefManager(requireContext())
            val userId = sharedPrefManager.getUserId()

            // Call deleteUserAccount() with the retrieved user ID
            deleteUserAccount(userId)
        }.apply {
            // Disable the "Yes" button initially
            setCancelable(false)
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()

        // Disable the "Yes" button initially
        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.isEnabled = false


        positiveButton.setTextColor(resources.getColor(R.color.grey))


        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(resources.getColor(R.color.background))

        // Add an OnCheckedChangeListener to the checkbox
        confirmationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            positiveButton.isEnabled = isChecked

            // Update the "Yes" button color based on the checkbox state
            if (isChecked) {
                positiveButton.setTextColor(resources.getColor(R.color.background))
            } else {
                positiveButton.setTextColor(resources.getColor(R.color.grey))
            }
        }
    }

    private fun deleteUserAccount(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                val sharedPrefManager = SharedPrefManager(requireContext())
                val userUID = sharedPrefManager.getUserUID()

                // Call deleteUserAuthentication() with the retrieved user UID
                deleteUserAuthentication(userUID)

                sharedPrefManager.setLoggedIn(false)

                val intent = Intent(requireActivity(), DivideBegin::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete user account: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun deleteUserAuthentication(userUID: String) {
        val user = mAuth.currentUser
        if (user != null && user.uid == userUID) {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "User account deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete user account: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }


}