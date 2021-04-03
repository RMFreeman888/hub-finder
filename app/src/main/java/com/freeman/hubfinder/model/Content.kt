package com.freeman.hubfinder.model

import com.google.gson.annotations.SerializedName

data class Content(
        @SerializedName("name")
        val name: String,
        @SerializedName("html_url")
        val htmlUrl: String
)
