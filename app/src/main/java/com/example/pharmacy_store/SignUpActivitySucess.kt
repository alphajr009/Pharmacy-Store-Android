package com.example.pharmacy_store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class SignUpActivitySucess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_sucess)



        val button = findViewById<Button>(R.id.buttonsign)
        button.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}