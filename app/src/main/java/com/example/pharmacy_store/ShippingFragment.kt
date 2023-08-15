import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.pharmacy_store.Models.CartModel
import com.example.pharmacy_store.Models.ItemModel
import com.example.pharmacy_store.Models.OrderModel
import com.example.pharmacy_store.PaymentStrip
import com.example.pharmacy_store.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal

class ShippingFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var city: String
    private lateinit var postalCode: String
    private lateinit var totalPrice: String // Declare totalPrice as a member variable
    private lateinit var cartItems: MutableList<CartModel>


    private val db = FirebaseFirestore.getInstance() // Firestore instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_shipping, container, false)

        val toolbar: MaterialToolbar = rootView.findViewById(R.id.cartActualToolbar)

        val sharedPrefManager = SharedPrefManager(requireContext())
        val userId = sharedPrefManager.getUserId()


        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()

        }

        val args = arguments
        if (args != null && args.containsKey("totalPrice")) {
            totalPrice = args.getString("totalPrice", "0") // Assign the value to totalPrice
            val totalPriceValue = BigDecimal(totalPrice)

            updateTotalPrice(totalPriceValue)
        }

        val nameEditText: EditText = rootView.findViewById(R.id.nameEditText)
        val addressEditText: EditText = rootView.findViewById(R.id.addressEditText)
        val cityEditText: EditText = rootView.findViewById(R.id.citydEditText)
        val postalCodeEditText: EditText = rootView.findViewById(R.id.postalCodeEditText)

        nameEditText.addTextChangedListener {
            name = it.toString()
        }

        addressEditText.addTextChangedListener {
            address = it.toString()
        }

        cityEditText.addTextChangedListener {
            city = it.toString()
        }

        postalCodeEditText.addTextChangedListener {
            postalCode = it.toString()
        }

        val checkoutButton: AppCompatButton = rootView.findViewById(R.id.btnCartCheckout)
        checkoutButton.setOnClickListener {
            saveOrderToFirestore()

//            val intent = Intent(activity, PaymentStrip::class.java)
//            intent.putExtra("totalPrice", totalPrice)
//            intent.putExtra("userId", userId)
//            startActivity(intent)

        }

        // Populate cartItems with actual cart items (Assuming you have a method to get cart items)
        cartItems = getCartItems()

        return rootView
    }

     fun updateTotalPrice(totalPriceValue: BigDecimal) {
        val tvLastTotalPrice: TextView = rootView.findViewById(R.id.LastTotalPriceShipping)
        tvLastTotalPrice.text = "Rs. ${totalPriceValue.toPlainString()}"
    }

    private fun saveOrderToFirestore() {
        val sharedPrefManager = SharedPrefManager(requireContext())
        val userId = sharedPrefManager.getUserId() // Assuming you have a method to get the user ID

        val items = cartItems.map { cartItem ->
            ItemModel(itemName = cartItem.name, quantity = cartItem.quantity)
        }

        val order = OrderModel(
            uid = userId,
            address = address,
            city = city,
            postalcode = postalCode,
            name = name,
            totalPrice = totalPrice,
            items = items
        )

        val orderCollection = db.collection("orders")
        val newOrderDocument = orderCollection.add(order)

        newOrderDocument
            .addOnSuccessListener { documentReference ->
                val documentId = documentReference.id // This is the ID of the newly created document

                // Now create the intent and pass the documentId to PaymentStrip activity
                val intent = Intent(activity, PaymentStrip::class.java)
                intent.putExtra("totalPrice", totalPrice)
                intent.putExtra("userId", userId)
                intent.putExtra("documentId", documentId) // Pass the documentId as an extra
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                // Handle failure, e.g., show an error message to the user
            }
    }


    private fun getCartItems(): MutableList<CartModel> {
        val sharedPrefManager = SharedPrefManager(requireContext())
        val userId = sharedPrefManager.getUserId() // Assuming you have a method to get the user ID

        val cartItemList = mutableListOf<CartModel>() // Create a new list to populate

        db.collection("cart")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cartItem = document.toObject(CartModel::class.java)
                    cartItemList.add(cartItem)
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, e.g., show an error message to the user
            }

        return cartItemList // Return the populated list
    }
}
