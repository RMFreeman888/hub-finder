package com.freeman.hubfinder.model

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface GithubApi {
    @GET("/search/repositories")
    fun searchRepositories(@Query("q") search: String,
                           @Query("per_page") per_page: String):
                           Single<RepoSearchResponse>

    @GET("/repos/{owner}/{name}/contents")
    fun getRepoContent(@Path("owner") owner: String,
                       @Path("name") name:String):
                       Single<List<Content>>
}