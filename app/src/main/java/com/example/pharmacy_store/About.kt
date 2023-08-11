package com.example.pharmacy_store

import SharedPrefManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import android.content.Intent

class About : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)


        db = FirebaseFirestore.getInstance()


        return view
    }

    override fun onResume() {
        super.onResume()

        // Retrieve user information and update TextViews
        val view = view ?: return
        val sharedPrefManager = SharedPrefManager(requireContext())
        val userId = sharedPrefManager.getUserId()
        Log.d("About", "User ID: $userId")
        getUserInfoAndUpdateUI(userId, view)
    }



    private fun getUserInfoAndUpdateUI(userId: String, view: View) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                Log.d("About", "Document snapshot: $documentSnapshot")
                val name = documentSnapshot.getString("name") ?: ""
                val email = documentSnapshot.getString("email") ?: ""


                val nameTextView = view.findViewById<TextView>(R.id.Name)
                val phoneTextView = view.findViewById<TextView>(R.id.Email)


                nameTextView.text = name
                phoneTextView.text = email

            }
            .addOnFailureListener { e ->
                Log.e("About", "Error fetching user data", e)
            }
    }




}