package com.example.pharmacy_store

import SharedPrefManager
import ShippingFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacy_store.Adapters.CartAdapter
import com.example.pharmacy_store.Adapters.TotalPriceListener
import com.example.pharmacy_store.Models.CartModel
import com.example.pharmacy_store.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal

class CartFragment : Fragment(), TotalPriceListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter

    private var totalPrice: BigDecimal = BigDecimal.ZERO


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

        binding.cartActualToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }


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
                cartAdapter.totalPriceListener = this

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


        binding.btnCartCheckout.setOnClickListener {
            val shippingFragment = ShippingFragment()

            // Pass the total price to ShippingFragment using arguments
            val args = Bundle()
            args.putString("totalPrice", totalPrice.toPlainString())
            shippingFragment.arguments = args

            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.framelayout, shippingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root
    }

    override fun onTotalPriceUpdated(totalPrice: BigDecimal) {
        // Store the totalPrice value
        this.totalPrice = totalPrice

        // Update the TextView in the ShippingFragment (if it's visible)
        val shippingFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.framelayout)
        if (shippingFragment is ShippingFragment) {
            shippingFragment.updateTotalPrice(totalPrice)
        }
    }

}
