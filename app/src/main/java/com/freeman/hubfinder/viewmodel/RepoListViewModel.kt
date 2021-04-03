package com.freeman.hubfinder.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeman.hubfinder.model.Content
import com.freeman.hubfinder.model.GithubRepo
import com.freeman.hubfinder.model.RemoteRepository
import com.freeman.hubfinder.model.RepoSearchResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

@HiltViewModel
class RepoListViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val disposable: CompositeDisposable
): ViewModel() {

    private var lastSearched: String? = null

    val repoList = MutableLiveData<List<GithubRepo>>()
    val isLoading = MutableLiveData<Boolean>()
    val repoListLoadError = MutableLiveData<Boolean>()
    val repoDetails = MutableLiveData<GithubRepo>()
    val readme = MutableLiveData<String>()

    fun refresh() {
        if (lastSearched == null) {
            fetchRepos("android")
        } else {
            fetchRepos(lastSearched!!)
        }
    }

    fun fetchRepos(search: String) {
        lastSearched = search
        isLoading.value = true
        disposable.add(
                remoteRepository.searchRepositories(search)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object: DisposableSingleObserver<RepoSearchResponse>(){
                            override fun onSuccess(result: RepoSearchResponse?) {
                                repoList.value = result?.items
                                repoListLoadError.value = false
                                isLoading.value = false
                            }

                            override fun onError(e: Throwable?) {
                                isLoading.value = false
                                repoListLoadError.value = true
                            }
                        })
        )
    }

    fun getRepositoryDetails(id: Long) {
        val repoDetail = repoList.value?.find { it.id == id }
        repoDetails.value = repoDetail
            if (repoDetail == null) {
                return
            }
            disposable.add(
                    remoteRepository.getRepoContent(repoDetail.owner.login, repoDetail.name)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object: DisposableSingleObserver<List<Content>>(){
                                override fun onSuccess(result: List<Content>) {
                                    val repoReadme = result.find { it.name.equals("readme.md", true)}
                                    if(repoReadme !=null) {
                                        readme.value = repoReadme.htmlUrl
                                    }
                                }

                                override fun onError(e: Throwable?) {
                                    // TODO handle errors
                                }
                            })
            )

    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}