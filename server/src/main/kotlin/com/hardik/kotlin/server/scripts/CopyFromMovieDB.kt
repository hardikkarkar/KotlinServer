package com.hardik.kotlin.server.scripts

import com.hardik.kotlin.server.data.DatabaseConfig
import com.hardik.kotlin.server.data.DatabaseFactory
import com.hardik.kotlin.server.model.movie.MoviesDetailResponse
import com.hardik.kotlin.server.repository.MovieRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger

// Replace with your TMDb API key
const val TMDB_API_KEY = "82ca5982f2873c2d38a664b67f135d79"
const val BASE_TMDB_URL = "https://api.themoviedb.org/3"

// Data class to represent the list of popular movies from TMDb
@kotlinx.serialization.Serializable
data class PopularMoviesResponse(
    val page: Int,
    val results: List<MovieResult>,
    val total_pages: Int,
    val total_results: Int
)

@kotlinx.serialization.Serializable
data class MovieResult(
    val id: Int
)

fun main() = runBlocking {
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
    val movieRepository = MovieRepository()
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val totalMoviesToFetch = 20
    val moviesFetched = AtomicInteger(0)
    var currentPage = 1

    println("Starting to fetch and insert $totalMoviesToFetch movies...")

    while (moviesFetched.get() < totalMoviesToFetch) {
        try {
            val popularMoviesResponse: PopularMoviesResponse = httpClient.get("$BASE_TMDB_URL/movie/popular") {
                parameter("api_key", TMDB_API_KEY)
                parameter("page", currentPage)
            }.body()

            println("Fetched page $currentPage with ${popularMoviesResponse.results.size} movies.")

            for (movieResult in popularMoviesResponse.results) {
                if (moviesFetched.get() < totalMoviesToFetch) {
                    try {
                        val movieDetail: MoviesDetailResponse = httpClient.get("$BASE_TMDB_URL/movie/${movieResult.id}") {
                            parameter("api_key", TMDB_API_KEY)
                        }.body()

                        val generatedId = movieRepository.create(movieDetail)
                        if (generatedId != null) {
                            println("Inserted movie with TMDb ID ${movieResult.id} (Local ID: $generatedId)")
                            moviesFetched.incrementAndGet()
                        } else {
                            println("Failed to insert movie with TMDb ID ${movieResult.id}")
                        }
                        delay(500) // Small delay to respect rate limits (adjust as needed)
                    } catch (e: Exception) {
                        println("Error fetching details for movie ID ${movieResult.id}: ${e.message}")
                    }
                } else {
                    break
                }
            }

            currentPage++
            if (popularMoviesResponse.page >= popularMoviesResponse.total_pages) {
                println("Reached the end of popular movies. Fetched ${moviesFetched.get()} movies.")
                break
            }
            delay(1000) // Delay between pages
        } catch (e: Exception) {
            println("Error fetching popular movies page $currentPage: ${e.message}")
            break
        }
    }

    httpClient.close()
    println("Finished fetching and attempting to insert movies. Total inserted: ${moviesFetched.get()}.")
}