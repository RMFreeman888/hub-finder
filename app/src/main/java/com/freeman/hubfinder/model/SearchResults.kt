package com.freeman.hubfinder.model

import com.google.gson.annotations.SerializedName

data class SearchResults(
    @SerializedName("items")
    val items: List<GithubRepo>?
)
