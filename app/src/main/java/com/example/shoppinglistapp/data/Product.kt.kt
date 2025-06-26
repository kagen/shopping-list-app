package com.example.shoppinglistapp.data

data class Product(
    val name: String,
    var icon: String? = null,
    var quantity: Int = 1,
    var memo: String? = null,
    var isChecked: Boolean = false
)
