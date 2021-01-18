package com.balajiss.movie.model.display

import com.google.gson.annotations.SerializedName

data class RatingItem(

    @SerializedName("Source")
    val source: String,

    @SerializedName("Value")
    val value: String
)