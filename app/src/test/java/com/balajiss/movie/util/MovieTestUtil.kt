package com.balajiss.movie.util

import com.balajiss.movie.model.display.MovieDisplayResponse
import com.balajiss.movie.model.display.RatingItem
import com.balajiss.movie.model.search.MovieItem

object MovieTestUtil {

    fun mockMovie() = MovieItem(
        "Captain Marvel",
        "2019",
        "tt4154664",
        "Movie",
        "https://m.media-amazon.com/images/M/MV5BMTE0YWFmOTMtYTU2ZS00ZTIxLWE3OTEtYTNiYzBkZjViZThiXkEyXkFqcGdeQXVyODMzMzQ4OTI@._V1_SX300.jpg"
    )

    fun mockMovieList() = listOf(mockMovie())

    fun mockRating() = RatingItem(
        "Internet Movie Database",
        "6.9/10"
    )

    fun mockMovieDetail() = MovieDisplayResponse(
        "True",
    "",
    "Captain Marvel",
    "2019",
    "PG-13",
    "08 Mar 2019",
    "123 min",
    "Action, Adventure, Sci-Fi",
    "Anna Boden, Ryan Fleck",
    "Anna Boden (screenplay by), Ryan Fleck (screenplay by), Geneva Robertson-Dworet (screenplay by), Nicole Perlman (story by), Meg LeFauve (story by), Anna Boden (story by), Ryan Fleck (story by), Geneva Robertson-Dworet (story by)",
    "Brie Larson, Samuel L. Jackson, Ben Mendelsohn, Jude Law",
    "Carol Danvers becomes one of the universe's most powerful heroes when Earth is caught in the middle of a galactic war between two alien races.",
    "English",
    "USA, Australia",
    "8 wins & 48 nominations.",
    "https://m.media-amazon.com/images/M/MV5BMTE0YWFmOTMtYTU2ZS00ZTIxLWE3OTEtYTNiYzBkZjViZThiXkEyXkFqcGdeQXVyODMzMzQ4OTI@._V1_SX300.jpg",
    listOf(mockRating()),
    "64",
    "6.9",
    "437,802",
    "tt4154664",
    "movie",
    "N/A",
    "$426,829,839",
    "Marvel Studios",
    "N/A"
    )
}