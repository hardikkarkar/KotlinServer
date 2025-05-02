package com.hardik.kotlin.server.model.movie

import kotlinx.serialization.Serializable

@Serializable
data class MoviesDetailResponse(
    val adult: Boolean? = null,
    val backdrop_path: String? = null,
    val belongs_to_collection: BelongsToCollection? = null,
    val budget: Int? = null,
    val genres: List<Genre>? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val imdb_id: String? = null,
    val original_language: String? = null,
    val original_title: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val poster_path: String? = null,
    val production_companies: List<ProductionCompany>? = null,
    val production_countries: List<ProductionCountry>? = null,
    val release_date: String? = null,
    val revenue: Int? = null,
    val runtime: Int? = null,
    val spoken_languages: List<SpokenLanguage>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val vote_average: Double? = null,
    val vote_count: Int? = null
)

@Serializable
data class BelongsToCollection(
    val backdrop_path: String? = null,
    val id: Int,
    val name: String? = null,
    val poster_path: String? = null
)

@Serializable
data class Genre(
    val id: Int,
    val name: String? = null
)

@Serializable
data class ProductionCompany(
    val id: Int,
    val logo_path: String? = null,
    val name: String? = null,
    val origin_country: String? = null
)

@Serializable
data class ProductionCountry(
    val iso_3166_1: String? = null,
    val name: String? = null
)

@Serializable
data class SpokenLanguage(
    val english_name: String? = null,
    val iso_639_1: String? = null,
    val name: String? = null
)

//Domain

@Serializable
data class MovieDetailDomain(
    val adult: Boolean? = null,
    val backdrop_path: String? = null,
    val belongs_to_collection: BelongsToCollectionDomain? = null,
    val budget: Int? = null,
    val genres: List<GenreDomain>? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val imdb_id: String? = null,
    val original_language: String? = null,
    val original_title: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val poster_path: String? = null,
    val production_companies: List<ProductionCompanyDomain>? = null,
    val production_countries: List<ProductionCountryDomain>? = null,
    val release_date: String? = null,
    val revenue: Int? = null,
    val runtime: Int? = null,
    val runtimeWithMinutes: String? = null,
    val spoken_languages: List<SpokenLanguageDomain>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val vote_average: Double? = null,
    val vote_count: Int? = null
)

@Serializable
data class BelongsToCollectionDomain(
    val backdrop_path: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val poster_path: String? = null,
)

@Serializable
data class GenreDomain(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class ProductionCompanyDomain(
    val id: Int? = null,
    val logo_path: String? = null,
    val name: String? = null,
    val origin_country: String? = null,
)

@Serializable
data class ProductionCountryDomain(
    val iso_3166_1: String? = null,
    val name: String? = null,
)

@Serializable
data class SpokenLanguageDomain(
    val english_name: String? = null,
    val iso_639_1: String? = null,
    val name: String? = null,
)