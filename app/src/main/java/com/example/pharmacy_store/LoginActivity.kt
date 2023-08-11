package com.example.pharmacy_store

import SharedPrefManager
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Check if user is already logged in
        val sharedPrefManager = SharedPrefManager(this)
        if (sharedPrefManager.isLoggedIn()) {
            startActivity(Intent(this, UserLandingPage::class.java))
            finish() // Close the LoginActivity
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val signup = findViewById<TextView>(R.id.textView)
        signup.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {

                    val userUID = auth.currentUser?.uid

                    // Save the user UID to Shared Preferences
                    val sharedPrefManager = SharedPrefManager(this)
                    sharedPrefManager.saveUserUID(userUID)


                    // Sign in success, update UI with the signed-in user's information
                    db.collection("users")
                        .whereEqualTo("email", email) // Search the users collection using the email address
                        .get()
                        .addOnCompleteListener { userTask: Task<QuerySnapshot> ->
                            if (userTask.isSuccessful) {
                                val documents = userTask.result?.documents
                                if (documents != null && documents.isNotEmpty()) {
                                    val userId = documents[0].id

                                    // Save login status
                                    val sharedPrefManager = SharedPrefManager(this)
                                    sharedPrefManager.setLoggedIn(true)

                                    // Save user's ID
                                    sharedPrefManager.saveUserId(userId)

                                    // Navigate to the UserLandingPage
                                    startActivity(Intent(this, UserLandingPage::class.java))
                                } else {
                                    // No matching document, show user not found message
                                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // If the task is unsuccessful, show an error message
                                Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    when (errorCode) {
                        "ERROR_USER_NOT_FOUND" -> {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                        "ERROR_WRONG_PASSWORD" -> {
                            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
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

}
