package com.example.pharmacy_store

import SharedPrefManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DivideBegin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)



        if (isFirstLaunch) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }else {
            startActivity(Intent(this, LoginActivity::class.java))
//            val sharedPrefManager = SharedPrefManager(this)
//            if (sharedPrefManager.isLoggedIn()) {
//                startActivity(Intent(this, UserLandingPage::class.java))
//                finish() // Close the DivideActivity
//            }

        }



    }

}