package com.hardik.kotlin.server.model.movie

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val page: Int,
    val results: List<MovieDetailDomain>,
    val total_pages: Int,
    val total_results: Int
)

@Serializable
data class MovieEntity(
    val id: Int,
    val poster_path: String,
    val overview: String,
    val title: String,
    val vote_average: Float,
    val release_date: String? = null,
)

fun MovieDetailDomain.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = this.id ?: -1, // Handle potential null ID
        poster_path = this.poster_path ?: "",
        overview = this.overview ?: "",
        title = this.title ?: "",
        vote_average = this.vote_average?.toFloat() ?: 0f,
        release_date = this.release_date
    )
}