package com.freeman.hubfinder.di

import com.freeman.hubfinder.model.GithubApi
import com.freeman.hubfinder.model.RemoteRepository
import com.freeman.hubfinder.view.adapter.RepoListAdapter
import com.freeman.hubfinder.viewmodel.RepoListViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule() {
    private val BASE_URL = "https://api.github.com"

    @Singleton
    @Provides
    fun provideGithubApi(): GithubApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }

    @Provides
    fun provideDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun provideRepoListViewModel(remoteRepository: RemoteRepository,
                                 disposable: CompositeDisposable): RepoListViewModel {
        return RepoListViewModel(remoteRepository, disposable)
    }

    @Singleton
    @Provides
    fun provideRemoteRepository(githubApi: GithubApi): RemoteRepository {
        return RemoteRepository(githubApi)
    }

    @Provides
    fun provideRepoListAdapter() = RepoListAdapter(arrayListOf())
}