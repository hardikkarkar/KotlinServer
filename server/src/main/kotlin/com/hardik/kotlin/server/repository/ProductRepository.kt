package com.hardik.kotlin.server.repository

import com.hardik.kotlin.server.data.DatabaseFactory.dbQuery
import com.hardik.kotlin.server.data.tabels.Products
import com.hardik.kotlin.server.model.products.Product
import com.hardik.kotlin.server.utils.Constant.PRODUCT_DB_NAME
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ProductRepository {

    suspend fun getAll(): List<Product> = dbQuery (PRODUCT_DB_NAME){
        Products.selectAll().map { toProduct(it) }
    }

    suspend fun getById(id: Int): Product? = dbQuery (PRODUCT_DB_NAME) {
        Products.selectAll().where { Products.id eq id }
            .map { toProduct(it) }
            .singleOrNull()
    }

    suspend fun create(product: Product): Product = dbQuery (PRODUCT_DB_NAME) {
        val insertStatement = Products.insert {
            it[name] = product.name
            it[price] = product.price
        }
        val result = insertStatement.resultedValues?.singleOrNull()
        if (result != null) {
            toProduct(result)
        } else {
            throw Exception("Error creating product")
        }
    }

    suspend fun update(id: Int, product: Product): Product? = dbQuery (PRODUCT_DB_NAME) {
        println("Updating product with ID: $id")
        val updatedRows = Products.update({ Products.id eq id }) {
            it[name]= product.name
            it[price] = product.price
        }
        println("Updated rows: $updatedRows")
        val updatedProduct = Products.selectAll().where { Products.id eq id }
            .map { toProduct(it) }
            .singleOrNull()
        println("Updated product: $updatedProduct")
        updatedProduct
    }


    suspend fun delete(id: Int): Boolean = dbQuery (PRODUCT_DB_NAME) {
        return@dbQuery Products.deleteWhere { Products.id eq id } > 0
    }

    private fun toProduct(row: ResultRow): Product =
        Product(
            id = row[Products.id],
            name = row[Products.name],
            price = row[Products.price]
        )
}