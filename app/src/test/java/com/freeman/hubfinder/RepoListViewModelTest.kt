package com.freeman.hubfinder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.freeman.hubfinder.model.*
import com.freeman.hubfinder.viewmodel.RepoListViewModel
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class RepoListViewModelTest {

    @get: Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var remoteRepository: RemoteRepository
    @Mock
    lateinit var disposable: CompositeDisposable

    lateinit var repoListViewModel: RepoListViewModel

    private val owner = Owner("some_name","some_url")
    private val githubRepo = GithubRepo(1,"thanos/endgame","endgame", owner,
        1, 1,"Kotlin", 1, 1)
    private val githubRepos = arrayListOf(githubRepo)
    private val searchResults = RepoSearchResponse(githubRepos)
    private val content = Content("file_name", "html_url")
    private val contentList = arrayListOf(content)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repoListViewModel = RepoListViewModel(remoteRepository, disposable)
    }

    @Before
    fun setUpRxSchedulers() {
        val immediateScheduler: Scheduler = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true, true)
            }

            // This prevents errors when scheduling a delay
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

        }

        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediateScheduler}
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediateScheduler }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediateScheduler }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediateScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediateScheduler }
    }

    @Test
    fun fetchRepos_onSuccess_should_set_loading_false() {
        val testSingle: Single<RepoSearchResponse> = Single.just(searchResults)
        val testString = "hub_finder"

        Mockito.`when`(remoteRepository.searchRepositories(testString)).thenReturn(testSingle)

        repoListViewModel.fetchRepos(testString)

        Assert.assertEquals(false, repoListViewModel.isLoading.value)
    }


    @Test
    fun fetchRepos_onSuccess_should_set_error_null() {
        val testSingle: Single<RepoSearchResponse> = Single.just(searchResults)
        val testString = "hub_finder"

        Mockito.`when`(remoteRepository.searchRepositories(testString)).thenReturn(testSingle)

        repoListViewModel.fetchRepos(testString)

        Assert.assertEquals(null, repoListViewModel.repoListLoadError.value)
    }

    @Test
    fun fetchRepos_onSuccess_should_set_repoList() {
        val testSingle: Single<RepoSearchResponse> = Single.just(searchResults)
        val testString = "hub_finder"
        Mockito.`when`(remoteRepository.searchRepositories(testString)).thenReturn(testSingle)

        repoListViewModel.fetchRepos(testString)

        Assert.assertEquals(githubRepos, repoListViewModel.repoList.value)
    }

    @Test
    fun fetchRepos_onError_should_set_isLoading_false() {
        val testSingle: Single<RepoSearchResponse> = Single.error(Throwable())
        val testString = "hub_finder"

        Mockito.`when`(remoteRepository.searchRepositories(testString)).thenReturn(testSingle)

        repoListViewModel.fetchRepos(testString)

        Assert.assertEquals(false, repoListViewModel.isLoading.value)

    }

    @Test
    fun fetchRepos_onError_should_set_repoListLoadError() {
        val testThrowableMessage = "some_error"
        val throwable: Throwable = Throwable(testThrowableMessage)

        val testSingle: Single<RepoSearchResponse> = Single.error(throwable)
        val testString = "hub_finder"

        Mockito.`when`(remoteRepository.searchRepositories(testString)).thenReturn(testSingle)

        repoListViewModel.fetchRepos(testString)

        Assert.assertEquals(testThrowableMessage, repoListViewModel.repoListLoadError.value?.message)
    }
}