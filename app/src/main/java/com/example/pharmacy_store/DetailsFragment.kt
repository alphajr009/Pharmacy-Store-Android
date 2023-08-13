import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.pharmacy_store.R
import com.example.pharmacy_store.databinding.FragmentDetailsBinding
import com.example.shoeapp.Models.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val itemName = arguments?.getString("name", "") ?: ""
        val itemUsage = arguments?.getString("usage", "") ?: ""
        val itemComposition = arguments?.getString("composition", "") ?: ""
        val itemImageUrl = arguments?.getString("imageUrl", "") ?: ""
        val itemPrice = arguments?.getInt("price", 0) ?: 0
        val itemQuantity = arguments?.getInt("quantity", 0) ?: 0



        val stockAvailabilityTextView = binding.stockAvailability

        if (itemQuantity <= 6) {
            stockAvailabilityTextView.text = "Out of Stock"
            stockAvailabilityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else if (itemQuantity <= 12) {
            stockAvailabilityTextView.text = "Low Stock"
            stockAvailabilityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        } else {
            stockAvailabilityTextView.text = "In Stock"
            stockAvailabilityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        binding.tvDetailsProductName.text = itemName
        binding.usageDescirption.text = itemUsage
        binding.compositionDescirption.text = itemComposition
        binding.productPrice.text = "Rs. $itemPrice"
        Glide.with(requireContext()).load(itemImageUrl).into(binding.ivDetails)

        binding.detailActualToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageView2.setOnClickListener {
            val sharedPrefManager = SharedPrefManager(requireContext())
            val userId = sharedPrefManager.getUserId()

            if (itemQuantity <= 6) {
                Toast.makeText(requireContext(), "Item is out of stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                val cartItem = CartModel(
                    uid = userId,
                    imageUrl = itemImageUrl,
                    name = itemName,
                    price = itemPrice.toString(),
                    quantity = 1 // You can adjust the quantity as needed
                )

                db.collection("cart")
                    .document() // Use .document() to let Firestore generate a new document ID
                    .set(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Item added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }
}
