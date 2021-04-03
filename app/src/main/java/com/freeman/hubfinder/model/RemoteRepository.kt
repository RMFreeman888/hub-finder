package com.freeman.hubfinder.model

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val githubApi: GithubApi
) {
    private val perPage = "100"

    fun searchRepositories(search: String): Single<SearchResults> {
        return githubApi.searchRepositories(search, perPage)
    }
}