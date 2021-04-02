package com.freeman.hubfinder.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteRepository {
    private val BASE_URL = "https://api.github.com"
    private val api: GithubApi

    init {
        api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(GithubApi::class.java)
    }

    fun searchRepositories(search: String): Single<SearchResults> {
        return api.searchRepositories(search)
    }
}