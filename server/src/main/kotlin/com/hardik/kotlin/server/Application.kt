package com.hardik.kotlin.server

import com.hardik.kotlin.server.data.DatabaseConfig
import com.hardik.kotlin.server.data.DatabaseFactory
import com.hardik.kotlin.server.repository.MovieRepository
import com.hardik.kotlin.server.repository.ProductRepository
import com.hardik.kotlin.server.routes.movieRouting
import com.hardik.kotlin.server.routes.productRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
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
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    val productRepository = ProductRepository()
    val movieRepository = MovieRepository()
    routing {
        productRouting(productRepository)
        movieRouting(movieRepository)
    }
}