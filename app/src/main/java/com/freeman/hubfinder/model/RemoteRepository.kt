package com.freeman.hubfinder.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val githubApi: GithubApi
) {

    fun searchRepositories(search: String): Single<SearchResults> {
        return githubApi.searchRepositories(search)
    }
}