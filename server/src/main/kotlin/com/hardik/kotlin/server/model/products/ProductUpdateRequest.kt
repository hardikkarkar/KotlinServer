package com.hardik.kotlin.server.model.products

import kotlinx.serialization.Serializable

@Serializable
data class ProductUpdateRequest(
    val name: String,
    val price: Double
)