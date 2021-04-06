package com.freeman.hubfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeman.hubfinder.model.*
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

    private var DEFAULT_SEARCH = "android"
    private var BLANK_PAGE = "about:blank"
    private var README_REGEX = "readme"

    private var lastSearched: String? = null

    val repoList = MutableLiveData<List<GithubRepo>>()
    val isLoading = MutableLiveData<Boolean>()
    val repoListLoadError = MutableLiveData<Throwable>()
    val repoDetails = MutableLiveData<GithubRepo>()
    val readme = MutableLiveData<String>()
    val isLoadingReadme = MutableLiveData<Boolean>()
    val readmeLoadingError = MutableLiveData<Throwable>()
    val hasReadme = MutableLiveData<Boolean>()

    fun refresh() {
        if (lastSearched == null) {
            fetchRepos(DEFAULT_SEARCH)
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
                                repoListLoadError.value = null
                                isLoading.value = false
                            }

                            override fun onError(e: Throwable?) {
                                isLoading.value = false
                                repoListLoadError.value = e
                            }
                        })
        )
    }

    fun getRepositoryDetails(id: Long) {
        val repoDetail = repoList.value?.find { it.id == id }
        isLoadingReadme.value = true
        readme.value = BLANK_PAGE
        repoDetails.value = repoDetail
            if (repoDetail == null) {
                return
            }
        isLoadingReadme.value = true
            disposable.add(
                    remoteRepository.getRepoContent(repoDetail.owner.login, repoDetail.name)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object: DisposableSingleObserver<List<Content>>(){
                                override fun onSuccess(result: List<Content>) {
                                    isLoadingReadme.value = false
                                    val repoReadme = result.find { it.name.contains(README_REGEX, true)}
                                    if(repoReadme !=null) {
                                        hasReadme.value = true
                                        readme.value = repoReadme.htmlUrl
                                    } else {
                                        hasReadme.value = false
                                    }
                                }

                                override fun onError(e: Throwable?) {
                                    isLoadingReadme.value = false
                                    readmeLoadingError.value = e
                                }
                            })
            )

    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}