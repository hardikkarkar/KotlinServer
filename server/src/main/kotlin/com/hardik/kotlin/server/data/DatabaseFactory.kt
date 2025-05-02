package com.hardik.kotlin.server.data

import com.hardik.kotlin.server.data.tabels.BelongsToCollections
import com.hardik.kotlin.server.data.tabels.Genres
import com.hardik.kotlin.server.data.tabels.Movies
import com.hardik.kotlin.server.data.tabels.MoviesGenres
import com.hardik.kotlin.server.data.tabels.ProductionCompanies
import com.hardik.kotlin.server.data.tabels.ProductionCountries
import com.hardik.kotlin.server.data.tabels.Products
import com.hardik.kotlin.server.data.tabels.SpokenLanguages
import com.hardik.kotlin.server.utils.Constant.MOVIE_DB_NAME
import com.hardik.kotlin.server.utils.Constant.PRODUCT_DB_NAME
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val databases = mutableMapOf<String, Database>()
    fun init(configs: List<DatabaseConfig>) {

        configs.forEach { config ->
            val database = Database.connect(config.jdbcURL, config.driverClassName)
            databases[config.name] = database
            transaction(database) {
                when (config.name) {
                    MOVIE_DB_NAME -> SchemaUtils.create(
                        Movies,
                        BelongsToCollections,
                        Genres,
                        ProductionCompanies,
                        ProductionCountries,
                        SpokenLanguages,
                        MoviesGenres
                    )

                    PRODUCT_DB_NAME -> SchemaUtils.create(Products)
                    else -> throw IllegalArgumentException("Unknown database name: ${config.name}")
                }
            }
        }
    }

    suspend fun <T> dbQuery(dbName: String, block: suspend () -> T): T {
        val database =
            databases[dbName] ?: throw IllegalArgumentException("Unknown database name: $dbName")
        return newSuspendedTransaction(Dispatchers.IO, database) { block() }
    }
}