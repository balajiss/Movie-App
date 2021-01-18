package com.balajiss.movie.model

import com.google.gson.annotations.SerializedName

interface BaseResponse {

    val Response: String

    val Error: String
}