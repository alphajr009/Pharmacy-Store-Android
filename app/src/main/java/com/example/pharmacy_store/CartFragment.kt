package com.example.pharmacy_store

import SharedPrefManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacy_store.Adapters.CartAdapter
import com.example.pharmacy_store.databinding.FragmentCartBinding
import com.example.shoeapp.Models.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sharedPrefManager = SharedPrefManager(requireContext())
        val userId = sharedPrefManager.getUserId()

        val cartItems = mutableListOf<CartModel>()

        db.collection("cart")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cartItem = document.toObject(CartModel::class.java)
                    cartItem.docId = document.id // Save the document ID
                    cartItems.add(cartItem)
                }

                // Initialize and set up RecyclerView with CartAdapter
                cartAdapter = CartAdapter(cartItems, binding.tvLastSubTotalprice, binding.tvLastDeliveryprice, binding.tvLastTotalPrice)
                binding.rvCartItems.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = cartAdapter
                }

                // Calculate and update prices after fetching cart items
                cartAdapter.updatePrices()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

        return binding.root
    }
}
