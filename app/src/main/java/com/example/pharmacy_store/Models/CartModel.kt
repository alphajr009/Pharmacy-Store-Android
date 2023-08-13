package com.example.shoeapp.Models

data class CartModel(

    var docId: String = "",
    val uid: String = "", // User ID
    val imageUrl: String = "",
    val name: String = "",
    val price: String = "",
    var quantity: Int = 1

)