package com.freeman.hubfinder.model

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("avatar_url")
    val avatarUrl: String?
)
