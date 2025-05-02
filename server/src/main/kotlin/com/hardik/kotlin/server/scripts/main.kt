package com.hardik.kotlin.server.scripts

import com.hardik.kotlin.server.data.DatabaseConfig
import com.hardik.kotlin.server.data.DatabaseFactory
import com.hardik.kotlin.server.data.DatabaseFactory.dbQuery
import com.hardik.kotlin.server.data.tabels.Products
import com.hardik.kotlin.server.utils.Constant.PRODUCT_DB_NAME
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert

suspend fun main() {
    // Initialize the database
    DatabaseFactory.init(
        listOf(
            DatabaseConfig(
                name = "movies",
                driverClassName = "org.sqlite.JDBC",
                jdbcURL = "jdbc:sqlite:./movies.db"
            ),
            DatabaseConfig(
                name = "products",
                driverClassName = "org.sqlite.JDBC",
                jdbcURL = "jdbc:sqlite:./products.db"
            )
        )
    )

    // Number of products to insert
    val numberOfProducts = 1000

    // Insert products in batches
    val batchSize = 100

    dbQuery(PRODUCT_DB_NAME) {
        // Create the table if it doesn't exist
        SchemaUtils.create(Products)

        repeat(numberOfProducts / batchSize) { batch ->
            Products.batchInsert((1..batchSize).toList()) { index ->
                val productNumber = batch * batchSize + index
                this[Products.name] = "Product $productNumber"
                this[Products.price] = 10.0 + productNumber
            }
            println("Inserted batch ${batch + 1} of $batchSize products")
        }
    }
    println("Finished inserting $numberOfProducts products")
}