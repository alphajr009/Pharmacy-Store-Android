package com.example.pharmacy_store.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacy_store.Models.CartModel
import com.example.pharmacy_store.databinding.CartproductItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal


interface TotalPriceListener {
    fun onTotalPriceUpdated(totalPrice: BigDecimal)
}
class CartAdapter(private val cartItems: MutableList<CartModel>,
                  private val subTotalView: TextView,
                  private val deliveryPriceView: TextView,
                  private val totalView: TextView) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    var totalPriceListener: TotalPriceListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            CartproductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        holder.binding.apply {
            CartProductName.text = currentItem.name
            CartProductPrice.text = "Rs. ${currentItem.price}"
            tvCartItemCount.text = currentItem.quantity.toString()
            Glide.with(root.context).load(currentItem.imageUrl).into(CartProduct)

            btnCartItemAdd.setOnClickListener {
                if (currentItem.quantity < 5) {
                    currentItem.quantity++
                    tvCartItemCount.text = currentItem.quantity.toString()
                    updateQuantityInFirestore(currentItem)
                    updatePrices()
                } else {
                    Toast.makeText(root.context, "Maximum quantity reached", Toast.LENGTH_SHORT).show()
                }
            }

            btnCartItemMinus.setOnClickListener {
                if (currentItem.quantity > 1) {
                    currentItem.quantity--
                    tvCartItemCount.text = currentItem.quantity.toString()
                    updateQuantityInFirestore(currentItem)
                    updatePrices()
                } else {
                    Toast.makeText(root.context, "Minimum quantity reached", Toast.LENGTH_SHORT).show()
                }
            }

            imageView3.setOnClickListener {
                // Delete item from Firestore and remove it from the list
                db.collection("cart").document(currentItem.docId).delete()
                    .addOnSuccessListener {
                        cartItems.removeAt(position)
                        notifyDataSetChanged()
                        updatePrices()
                        Toast.makeText(root.context, "Item removed successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(root.context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateQuantityInFirestore(item: CartModel) {
        db.collection("cart").document(item.docId)
            .update("quantity", item.quantity)
            .addOnSuccessListener {
                // Quantity updated in Firestore
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
    private var totalPrice: BigDecimal = BigDecimal.ZERO

    fun updatePrices() {
        var subtotal = BigDecimal.ZERO
        for (item in cartItems) {
            val price = BigDecimal(item.price)
            val quantity = BigDecimal(item.quantity)
            subtotal += price.multiply(quantity)
        }

        val deliveryPrice = BigDecimal(1000) // Fixed delivery price
        val totalPrice = subtotal + deliveryPrice

        subTotalView.text = "Rs. ${subtotal.toPlainString()}"
        deliveryPriceView.text = "Rs. ${deliveryPrice.toPlainString()}"
        totalView.text = "Rs. ${totalPrice.toPlainString()}"

        totalPriceListener?.onTotalPriceUpdated(totalPrice)

    }

    override fun getItemCount() = cartItems.size

    inner class CartViewHolder(val binding: CartproductItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}