package com.example.tubestatus.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

class TubeStatus {
    @SerializedName("id")
    val id: String = ""

    @SerializedName("name")
    val name: String? = ""
}

class TubeLine {
}

data class TubeStatusResult(val query: Query)
