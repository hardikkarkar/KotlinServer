package com.hardik.kotlin.server.repository

import com.hardik.kotlin.server.data.DatabaseFactory.dbQuery
import com.hardik.kotlin.server.data.tabels.BelongsToCollections
import com.hardik.kotlin.server.data.tabels.Genres
import com.hardik.kotlin.server.data.tabels.Movies
import com.hardik.kotlin.server.data.tabels.MoviesGenres
import com.hardik.kotlin.server.data.tabels.ProductionCompanies
import com.hardik.kotlin.server.data.tabels.ProductionCountries
import com.hardik.kotlin.server.data.tabels.SpokenLanguages
import com.hardik.kotlin.server.model.movie.BelongsToCollectionDomain
import com.hardik.kotlin.server.model.movie.GenreDomain
import com.hardik.kotlin.server.model.movie.MovieDetailDomain
import com.hardik.kotlin.server.model.movie.MoviesDetailResponse
import com.hardik.kotlin.server.model.movie.ProductionCompanyDomain
import com.hardik.kotlin.server.model.movie.ProductionCountryDomain
import com.hardik.kotlin.server.model.movie.SpokenLanguageDomain
import com.hardik.kotlin.server.utils.Constant.MOVIE_DB_NAME
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class MovieRepository() {

    suspend fun create(movieDetail: MoviesDetailResponse): Int? = dbQuery(MOVIE_DB_NAME) {
        val insertStatement = Movies.insert {
            it[adult] = movieDetail.adult
            it[backdropPath] = movieDetail.backdrop_path
            it[budget] = movieDetail.budget
            it[homepage] = movieDetail.homepage
            it[imdbId] = movieDetail.imdb_id
            it[originalLanguage] = movieDetail.original_language
            it[originalTitle] = movieDetail.original_title
            it[overview] = movieDetail.overview
            it[popularity] = movieDetail.popularity
            it[posterPath] = movieDetail.poster_path
            it[releaseDate] = movieDetail.release_date
            it[revenue] = movieDetail.revenue
            it[runtime] = movieDetail.runtime
            it[status] = movieDetail.status
            it[tagline] = movieDetail.tagline
            it[title] = movieDetail.title
            it[video] = movieDetail.video
            it[voteAverage] = movieDetail.vote_average
            it[voteCount] = movieDetail.vote_count
        }
        val generatedMovieId = insertStatement.resultedValues?.firstOrNull()?.get(Movies.id)

        generatedMovieId?.let { movieId ->
            movieDetail.belongs_to_collection?.let { collection ->
                BelongsToCollections.insert {
                    it[BelongsToCollections.id] = collection.id
                    it[BelongsToCollections.backdropPath] = collection.backdrop_path
                    it[BelongsToCollections.name] = collection.name
                    it[BelongsToCollections.posterPath] = collection.poster_path
                    it[BelongsToCollections.movieId] = movieId
                }
            }

            movieDetail.genres?.forEach { genre ->
                val existingGenre = Genres.select { Genres.id eq genre.id }.singleOrNull()
                if (existingGenre == null) {
                    Genres.insert {
                        it[Genres.id] = genre.id
                        it[Genres.name] = genre.name
                    }
                }
                MoviesGenres.insert {
                    it[MoviesGenres.movieId] = movieId
                    it[MoviesGenres.genreId] = genre.id
                }
            }

            movieDetail.production_companies?.forEach { company ->
                ProductionCompanies.insert {
                    it[ProductionCompanies.id] = company.id
                    it[ProductionCompanies.logoPath] = company.logo_path
                    it[ProductionCompanies.name] = company.name
                    it[ProductionCompanies.originCountry] = company.origin_country
                    it[ProductionCompanies.movieId] = movieId
                }
            }

            movieDetail.production_countries?.forEach { country ->
                ProductionCountries.insert {
                    it[ProductionCountries.iso31661] = country.iso_3166_1
                    it[ProductionCountries.name] = country.name
                    it[ProductionCountries.movieId] = movieId
                }
            }

            movieDetail.spoken_languages?.forEach { language ->
                SpokenLanguages.insert {
                    it[SpokenLanguages.englishName] = language.english_name
                    it[SpokenLanguages.iso6391] = language.iso_639_1
                    it[SpokenLanguages.name] = language.name
                    it[SpokenLanguages.movieId] = movieId
                }
            }
        }
        return@dbQuery generatedMovieId
    }

    suspend fun read(movieId: Int): MovieDetailDomain? = dbQuery(MOVIE_DB_NAME) {
        Movies.selectAll().where { Movies.id eq movieId }
            .map { rowToMovieDetailDomain(it) }
            .singleOrNull()
    }

    suspend fun update(movieId: Int, movieDetail: MoviesDetailResponse): Boolean = dbQuery(MOVIE_DB_NAME) {
        val updatedRows = Movies.update({ Movies.id eq movieId }) {
            it[adult] = movieDetail.adult
            it[backdropPath] = movieDetail.backdrop_path
            it[budget] = movieDetail.budget
            it[homepage] = movieDetail.homepage
            it[imdbId] = movieDetail.imdb_id
            it[originalLanguage] = movieDetail.original_language
            it[originalTitle] = movieDetail.original_title
            it[overview] = movieDetail.overview
            it[popularity] = movieDetail.popularity
            it[posterPath] = movieDetail.poster_path
            it[releaseDate] = movieDetail.release_date
            it[revenue] = movieDetail.revenue
            it[runtime] = movieDetail.runtime
            it[status] = movieDetail.status
            it[tagline] = movieDetail.tagline
            it[title] = movieDetail.title
            it[video] = movieDetail.video
            it[voteAverage] = movieDetail.vote_average
            it[voteCount] = movieDetail.vote_count
        }

        if (updatedRows > 0) {
            // Update related tables
            BelongsToCollections.deleteWhere { BelongsToCollections.movieId eq movieId }
            movieDetail.belongs_to_collection?.let { collection ->
                BelongsToCollections.insert {
                    it[id] = collection.id
                    it[backdropPath] = collection.backdrop_path
                    it[name] = collection.name
                    it[posterPath] = collection.poster_path
                    it[BelongsToCollections.movieId] = movieId
                }
            }

            // Update Genres
            MoviesGenres.deleteWhere { MoviesGenres.movieId eq movieId }
            movieDetail.genres?.forEach { genre ->
                val existingGenre = Genres.select { Genres.id eq genre.id }.singleOrNull()
                if (existingGenre == null) {
                    Genres.insert {
                        it[Genres.id] = genre.id
                        it[Genres.name] = genre.name
                    }
                }
                MoviesGenres.insert {
                    it[MoviesGenres.movieId] = movieId
                    it[MoviesGenres.genreId] = genre.id
                }
            }

            ProductionCompanies.deleteWhere { ProductionCompanies.movieId eq movieId }
            movieDetail.production_companies?.forEach { company ->
                ProductionCompanies.insert {
                    it[ProductionCompanies.id] = company.id
                    it[ProductionCompanies.logoPath] = company.logo_path
                    it[ProductionCompanies.name] = company.name
                    it[ProductionCompanies.originCountry] = company.origin_country
                    it[ProductionCompanies.movieId] = movieId
                }
            }

            ProductionCountries.deleteWhere { ProductionCountries.movieId eq movieId }
            movieDetail.production_countries?.forEach { country ->
                ProductionCountries.insert {
                    it[iso31661] = country.iso_3166_1
                    it[name] = country.name
                    it[ProductionCountries.movieId] = movieId
                }
            }

            SpokenLanguages.deleteWhere { SpokenLanguages.movieId eq movieId }
            movieDetail.spoken_languages?.forEach { language ->
                SpokenLanguages.insert {
                    it[englishName] = language.english_name
                    it[iso6391] = language.iso_639_1
                    it[name] = language.name
                    it[SpokenLanguages.movieId] = movieId
                }
            }

            true
        } else {
            false
        }
    }

    suspend fun delete(movieId: Int): Boolean = dbQuery(MOVIE_DB_NAME) {
        val deletedRows = Movies.deleteWhere { Movies.id eq movieId }
        if (deletedRows > 0) {
            // Delete related entries
            BelongsToCollections.deleteWhere { BelongsToCollections.movieId eq movieId }
            MoviesGenres.deleteWhere { MoviesGenres.movieId eq movieId } // Delete from the junction table
            ProductionCompanies.deleteWhere { ProductionCompanies.movieId eq movieId }
            ProductionCountries.deleteWhere { ProductionCountries.movieId eq movieId }
            SpokenLanguages.deleteWhere { SpokenLanguages.movieId eq movieId }
            true
        } else {
            false
        }
    }

    suspend fun getAll(): List<MovieDetailDomain> = dbQuery(MOVIE_DB_NAME) {
        Movies.selectAll().map { rowToMovieDetailDomain(it) }
    }

    private fun rowToMovieDetailDomain(row: ResultRow): MovieDetailDomain {
        val movieId = row[Movies.id]

        val belongsToCollection = BelongsToCollections.select { BelongsToCollections.movieId eq movieId }
            .map {
                BelongsToCollectionDomain(
                    backdrop_path = it[BelongsToCollections.backdropPath],
                    id = it[BelongsToCollections.id],
                    name = it[BelongsToCollections.name],
                    poster_path = it[BelongsToCollections.posterPath]
                )
            }.singleOrNull()

        val genres = MoviesGenres.join(Genres, onColumn = MoviesGenres.genreId, otherColumn = Genres.id, joinType = JoinType.INNER)
            .select { MoviesGenres.movieId eq movieId }
            .map { GenreDomain(it[Genres.id], it[Genres.name]) }

        val productionCompanies = ProductionCompanies.select { ProductionCompanies.movieId eq movieId }
            .map {
                ProductionCompanyDomain(
                    id = it[ProductionCompanies.id],
                    logo_path = it[ProductionCompanies.logoPath],
                    name = it[ProductionCompanies.name],
                    origin_country = it[ProductionCompanies.originCountry]
                )
            }

        val productionCountries = ProductionCountries.select { ProductionCountries.movieId eq movieId }
            .map {
                ProductionCountryDomain(
                    iso_3166_1 = it[ProductionCountries.iso31661],
                    name = it[ProductionCountries.name]
                )
            }

        val spokenLanguages = SpokenLanguages.select { SpokenLanguages.movieId eq movieId }
            .map {
                SpokenLanguageDomain(
                    english_name = it[SpokenLanguages.englishName],
                    iso_639_1 = it[SpokenLanguages.iso6391],
                    name = it[SpokenLanguages.name]
                )
            }

        return MovieDetailDomain(
            adult = row[Movies.adult],
            backdrop_path = row[Movies.backdropPath],
            belongs_to_collection = belongsToCollection,
            budget = row[Movies.budget],
            genres = genres,
            homepage = row[Movies.homepage],
            id = movieId,
            imdb_id = row[Movies.imdbId],
            original_language = row[Movies.originalLanguage],
            original_title = row[Movies.originalTitle],
            overview = row[Movies.overview],
            popularity = row[Movies.popularity],
            poster_path = row[Movies.posterPath],
            production_companies = productionCompanies,
            production_countries = productionCountries,
            release_date = row[Movies.releaseDate],
            revenue = row[Movies.revenue],
            runtime = row[Movies.runtime],
            runtimeWithMinutes = row[Movies.runtime]?.let { "${it} min" },
            spoken_languages = spokenLanguages,
            status = row[Movies.status],
            tagline = row[Movies.tagline],
            title = row[Movies.title],
            video = row[Movies.video],
            vote_average = row[Movies.voteAverage],
            vote_count = row[Movies.voteCount]
        )
    }
}