package com.hardik.kotlin.server.routes

import com.hardik.kotlin.server.model.movie.MovieResponse
import com.hardik.kotlin.server.model.movie.MoviesDetailResponse
import com.hardik.kotlin.server.model.movie.toMovieEntity
import com.hardik.kotlin.server.repository.MovieRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.movieRouting(movieRepository: MovieRepository) {
    route("/movies") {
        // GET /movies: Get all movies (with optional pagination/filtering)
        get {
            val movies = movieRepository.getAll()
            call.respond(movies)
        }

        // POST /movies: Create a new movie
        post {
            val movieDetailResponse = call.receive<MoviesDetailResponse>()
            val movieId = movieRepository.create(movieDetailResponse)
            if (movieId != null) {
                call.respond(HttpStatusCode.Created, mapOf("id" to movieId))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create movie")
            }
        }

        route("/{id}") {
            // GET /movies/{id}: Get a specific movie by ID
            get {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid movie ID")
                    return@get
                }
                val movie = movieRepository.read(id)
                if (movie != null) {
                    call.respond(movie)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Movie not found")
                }
            }

            // PUT /movies/{id}: Update an existing movie by ID
            put {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid movie ID")
                    return@put
                }
                val movieDetailResponse = call.receive<MoviesDetailResponse>()
                val updated = movieRepository.update(id, movieDetailResponse)
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Movie updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Movie not found")
                }
            }

            // DELETE /movies/{id}: Delete a movie by ID
            delete {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid movie ID")
                    return@delete
                }
                val deleted = movieRepository.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Movie deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Movie not found")
                }
            }
        }

        get("/all_movies") {
            val allMovieDetails = movieRepository.getAll()
            val movieResponse = MovieResponse(
                page = 1,
                results = allMovieDetails,
                total_pages = 1,
                total_results = allMovieDetails.size
            )
            call.respond(movieResponse)
        }
    }
}