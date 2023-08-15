package com.example.pharmacy_store.Models

data class OrderModel(
    val uid: String = "",
    val address: String = "",
    val city: String = "",
    val postalcode: String = "",
    val name: String = "",
    val totalPrice: String = "",
    val items: List<ItemModel> = emptyList()
)

data class ItemModel(
    val itemName: String = "",
    val quantity: Int = 0,
)
