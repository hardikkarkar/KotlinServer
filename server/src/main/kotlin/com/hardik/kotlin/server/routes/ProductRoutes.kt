package com.hardik.kotlin.server.routes

import com.hardik.kotlin.server.model.products.Product
import com.hardik.kotlin.server.model.products.ProductUpdateRequest
import com.hardik.kotlin.server.repository.ProductRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRouting(productRepository: ProductRepository) {
    route("/api/products") {
        get {
            call.respond(productRepository.getAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@get
            }
            val product = productRepository.getById(id)
            if (product != null) {
                call.respond(product)
            } else {
                call.respond(HttpStatusCode.NotFound, "Product not found")
            }
        }
        post {
            val product = call.receive<Product>()
            val createdProduct = productRepository.create(product)
            call.respond(HttpStatusCode.Created, createdProduct)
        }
        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@put
            }
            val productUpdateRequest = call.receive<ProductUpdateRequest>()
            val updatedProduct = productRepository.update(
                id,
                Product(id, productUpdateRequest.name, productUpdateRequest.price)
            )
            if (updatedProduct != null) {
                call.respond(updatedProduct)
            } else {
                call.respond(HttpStatusCode.NotFound, "Product not found")
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@delete
            }
            val deleted = productRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Product not found")
            }
        }
    }
}