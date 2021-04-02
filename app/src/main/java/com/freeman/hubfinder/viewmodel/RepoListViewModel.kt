package com.freeman.hubfinder.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeman.hubfinder.model.GithubRepo
import com.freeman.hubfinder.model.RemoteRepository
import com.freeman.hubfinder.model.SearchResults
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver


class RepoListViewModel: ViewModel() {

    private val remoteRepository = RemoteRepository()
    private val disposable = CompositeDisposable()

    val repoList = MutableLiveData<List<GithubRepo>>()
    val isLoading = MutableLiveData<Boolean>()
    val repoListLoadError = MutableLiveData<Boolean>()

    fun refresh() {
        fetchRepos("tetris")
    }

    fun fetchRepos(search: String) {
        isLoading.value = true
        disposable.add(
                remoteRepository.searchRepositories(search)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object: DisposableSingleObserver<SearchResults>(){
                            override fun onSuccess(result: SearchResults?) {
                                repoList.value = result?.items
                                repoListLoadError.value = false
                                isLoading.value = false
                            }

                            override fun onError(e: Throwable?) {
                                Log.e("TESTDEBUG", e.toString())
                                isLoading.value = false
                                repoListLoadError.value = true
                            }
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}