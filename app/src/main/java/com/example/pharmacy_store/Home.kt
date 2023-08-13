package com.example.pharmacy_store

import DetailsFragment
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pharmacy_store.Adapters.ItemDisplayAdapter
import com.example.pharmacy_store.Adapters.ProductOnClickInterface
import com.example.pharmacy_store.Models.Item
import com.example.pharmacy_store.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var itemAdapter: ItemDisplayAdapter
    private val db = FirebaseFirestore.getInstance()
    private var itemList: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupRecyclerView()

        // Set up a click listener on the parent layout to close the keyboard
        binding.root.setOnClickListener {
            hideSoftKeyboard()
        }

        return binding.root
    }

    private fun hideSoftKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setupRecyclerView() {
        // Set up the RecyclerView with a GridLayoutManager for 2 items per row
        binding.rvMainProductsList.layoutManager = GridLayoutManager(requireContext(), 2)
        itemAdapter = ItemDisplayAdapter(
            requireContext(),
            mutableListOf(),
            object : ProductOnClickInterface {
                override fun onClickProduct(item: Item) {
                    val detailsFragment = DetailsFragment().apply {
                        arguments = Bundle().apply {
                            putString("name", item.name)
                            putString("usage", item.usage)
                            putString("composition", item.composition)
                            putString("imageUrl", item.imageUrl)
                            putInt("price", item.Price ?: 0)
                            putInt("quantity", item.quantity ?: 0)
                        }
                    }

                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.framelayout, detailsFragment)
                    fragmentTransaction.addToBackStack(null) // Add this line to enable back navigation
                    fragmentTransaction.commit()
                }

            }
        )
        binding.rvMainProductsList.adapter = itemAdapter

        // Fetch all data from Firestore and update the adapter
        db.collection("items").get().addOnSuccessListener { result ->
            itemList.clear()
            for (document in result) {
                val item = document.toObject(Item::class.java)
                itemList.add(item)
            }
            itemAdapter.updateData(itemList)
        }

        // Set up search functionality
        binding.editTextScholUni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterItems(query: String) {
        val filteredList = itemList.filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
        itemAdapter.updateData(filteredList)
    }
}
