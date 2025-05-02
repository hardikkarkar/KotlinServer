package com.hardik.kotlin.server.data.tabels

import org.jetbrains.exposed.sql.Table

object Movies : Table() {
    val id = integer("id").autoIncrement()
    val adult = bool("adult").nullable()
    val backdropPath = varchar("backdrop_path", 255).nullable()
    val budget = integer("budget").nullable()
    val homepage = varchar("homepage", 255).nullable()
    val imdbId = varchar("imdb_id", 255).nullable()
    val originalLanguage = varchar("original_language", 255).nullable()
    val originalTitle = varchar("original_title", 255).nullable()
    val overview = text("overview").nullable()
    val popularity = double("popularity").nullable()
    val posterPath = varchar("poster_path", 255).nullable()
    val releaseDate = varchar("release_date", 255).nullable()
    val revenue = integer("revenue").nullable()
    val runtime = integer("runtime").nullable()
    val status = varchar("status", 255).nullable()
    val tagline = varchar("tagline", 255).nullable()
    val title = varchar("title", 255).nullable()
    val video = bool("video").nullable()
    val voteAverage = double("vote_average").nullable()
    val voteCount = integer("vote_count").nullable()

    override val primaryKey = PrimaryKey(id)
}

object BelongsToCollections : Table() {
    val id = integer("id")
    val backdropPath = varchar("backdrop_path", 255).nullable()
    val name = varchar("name", 255).nullable()
    val posterPath = varchar("poster_path", 255).nullable()
    val movieId = integer("movie_id").references(Movies.id)

    override val primaryKey = PrimaryKey(id)
}

object Genres : Table() {
    val id = integer("id")
    val name = varchar("name", 255).nullable()
    val movieId = integer("movie_id").references(Movies.id)

    override val primaryKey = PrimaryKey(id)
}

object ProductionCompanies : Table() {
    val id = integer("id")
    val logoPath = varchar("logo_path", 255).nullable()
    val name = varchar("name", 255).nullable()
    val originCountry = varchar("origin_country", 255).nullable()
    val movieId = integer("movie_id").references(Movies.id)

    override val primaryKey = PrimaryKey(id)
}

object ProductionCountries : Table() {
    val iso31661 = varchar("iso_3166_1", 255).nullable()
    val name = varchar("name", 255).nullable()
    val movieId = integer("movie_id").references(Movies.id)

    override val primaryKey = PrimaryKey(iso31661, movieId)
}

object SpokenLanguages : Table() {
    val englishName = varchar("english_name", 255).nullable()
    val iso6391 = varchar("iso_639_1", 255).nullable()
    val name = varchar("name", 255).nullable()
    val movieId = integer("movie_id").references(Movies.id)

    override val primaryKey = PrimaryKey(iso6391, movieId)
}