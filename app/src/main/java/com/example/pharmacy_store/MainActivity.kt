package com.example.pharmacy_store

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacy_store.DivideBegin


class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_screen)

        handler.postDelayed({
            startActivity(Intent(this, DivideBegin::class.java))
            finish()
        }, 1500L)


    }
}