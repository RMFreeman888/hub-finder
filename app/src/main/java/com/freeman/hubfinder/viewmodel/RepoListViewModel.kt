package com.freeman.hubfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeman.hubfinder.model.GithubRepo

class RepoListViewModel: ViewModel() {
    val repoList = MutableLiveData<List<GithubRepo>>()
    val isLoading = MutableLiveData<Boolean>()
    val repoListLoadError = MutableLiveData<Boolean>()

    fun refresh() {
        fetchRepos()
    }

    private fun fetchRepos() {
        // TODO: 01/04/2021 Remove dummy list and implement networking
       val dummyList = listOf<GithubRepo>(
           GithubRepo("A"),
           GithubRepo("B"),
           GithubRepo("C"),
           GithubRepo("D"),
           GithubRepo("E"),
           GithubRepo("F"),
           GithubRepo("G"),
           GithubRepo("H"),
           GithubRepo("I"),
           GithubRepo("J"),
           GithubRepo("K"),
           GithubRepo("L"),
           GithubRepo("M"),
           GithubRepo("N"),
           GithubRepo("O"),
           GithubRepo("P"),
           GithubRepo("Q")
       )
        isLoading.value = false
        repoListLoadError.value = false
        repoList.value = dummyList
    }
}