package com.balajiss.movie.model.search

import com.balajiss.movie.model.BaseResponse
import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    override val Response: String, override val Error: String,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Search")
    val searchResult: List<MovieItem>
) : BaseResponse