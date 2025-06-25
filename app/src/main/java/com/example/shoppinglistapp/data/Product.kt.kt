package com.example.shoppinglistapp.data

data class Product(
    val name: String,
    val icon: String? = null,
    val quantity: Int = 1,
    val memo: String? = null,
    val isChecked: Boolean = false
)
