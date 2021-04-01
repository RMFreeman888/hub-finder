package com.freeman.hubfinder.model

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    @SerializedName("name")
    val repoName: String?
)
