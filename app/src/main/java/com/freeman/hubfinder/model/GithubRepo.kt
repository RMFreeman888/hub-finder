package com.freeman.hubfinder.model

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    @SerializedName("full_name")
    val repoName: String?,
    @SerializedName("owner")
    val owner: Owner?
)
