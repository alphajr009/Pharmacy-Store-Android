package com.example.pharmacy_store.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacy_store.Models.Item
import com.example.pharmacy_store.databinding.DrugdisplaymainItemBinding

class ItemDisplayAdapter(
    private val context: Context,
    private var originalList: List<Item>, // Use a MutableList here
    private val productClickInterface: ProductOnClickInterface,
) : RecyclerView.Adapter<ItemDisplayAdapter.ViewHolder>() {

    private var filteredList: List<Item> = originalList.toMutableList()

    inner class ViewHolder(val binding: DrugdisplaymainItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DrugdisplaymainItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filteredList[position] // Use filteredList here
        holder.binding.tvNameShoeDisplayItem.text = " ${currentItem.name}"
        holder.binding.tvPriceShoeDisplayItem.text = "Rs. ${currentItem.Price}" // Use 'price' instead of 'Price'

        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivShoeDisplayItem)

        holder.itemView.setOnClickListener {
            productClickInterface.onClickProduct(filteredList[position])
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    // Function to update the adapter's data
    fun updateData(newList: List<Item>) {
        originalList = newList
        filterList("") // Reset filter when data is updated
    }

    // Function to filter the adapter's data
    fun filterList(query: String) {
        filteredList = originalList.filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }
}

interface ProductOnClickInterface {
    fun onClickProduct(item: Item)
}
