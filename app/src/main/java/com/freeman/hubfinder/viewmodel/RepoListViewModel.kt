package com.freeman.hubfinder.viewmodel

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
    val isLoadingReadme = MutableLiveData<Boolean>()
    val noReadmeAvailable = MutableLiveData<Boolean>()

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
        isLoadingReadme.value = true
        readme.value = "about:blank"
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
                                    val repoReadme = result.find { it.name.contains("readme", true)}
                                    if(repoReadme !=null) {
                                        readme.value = repoReadme.htmlUrl
                                    } else {
                                        noReadmeAvailable.value = true
                                    }
                                }

                                override fun onError(e: Throwable?) {
                                    isLoadingReadme.value = false
                                }
                            })
            )

    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}