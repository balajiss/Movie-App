package com.balajiss.movie.model.search

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieItem(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbId: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String
): Serializable