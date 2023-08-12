package com.example.pharmacy_store.Models

data class Item(
    val name: String = "",
    val quantity: Int? = null ,
    val usage: String = "",
    val composition: String = "",
    val Price: Int? = null,
    val imageUrl: String = ""
)
