package com.hardik.kotlin.server.model.products

import kotlinx.serialization.Serializable


@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double
)