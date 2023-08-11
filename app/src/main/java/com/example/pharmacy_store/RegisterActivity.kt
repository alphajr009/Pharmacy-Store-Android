package com.example.pharmacy_store

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this)

        // Initialize FirebaseAuth and FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        // Get references to the EditText fields
        val editTextName = findViewById<EditText>(R.id.nameEditText)
        val editTextEmail = findViewById<EditText>(R.id.emailEditText2)
        val editTextPassword = findViewById<EditText>(R.id.passwordEditText)
        val editTextConfirmPassword = findViewById<EditText>(R.id.cpasswordEditText)



        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {

            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            val user = User(
                name = name ?: "",
                email = email ?: "",
                password = password ?: "",
            )


            // Check if all fields are filled
            if (name.isNotEmpty() && email.isNotEmpty()  && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                // Validate email address
                if (isEmailValid(email)) {
                    // Validate password
                    if (isPasswordValid(password)) {
                        // Check if user already exists
                        checkUserExists(email) { exists ->
                            if (!exists) {
                                // Check if the password and confirm password match
                                if (password == confirmPassword) {
                                    createUserWithEmailAndPassword(user, email, password)
                                } else {
                                    // Show a warning that the passwords must match
                                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "User already exists, Sign In", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show a warning that all fields must be filled
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val signin = findViewById<TextView>(R.id.textView)
        signin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$".toRegex(RegexOption.IGNORE_CASE)
        return emailRegex.matches(email)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun checkUserExists(email: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.size() > 0)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking user existence: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createUserWithEmailAndPassword(user: User, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore(user)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "User added with ID: ${documentReference.id}")
                startActivity(Intent(this, SignUpActivitySucess::class.java))
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding user", e)
            }
    }
}


data class User(
    val name: String,
    val email: String,
    val password: String,
)
