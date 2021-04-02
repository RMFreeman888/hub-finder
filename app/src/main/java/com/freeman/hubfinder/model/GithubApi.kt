package com.freeman.hubfinder.model

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("/search/repositories")
    fun searchRepositories(@Query("q") search: String): Single<SearchResults>
}