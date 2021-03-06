package com.freeman.hubfinder.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.freeman.hubfinder.R
import com.freeman.hubfinder.databinding.ActivityItemListBinding
import com.freeman.hubfinder.databinding.RepoListBinding
import com.freeman.hubfinder.util.getErrorStringId
import com.freeman.hubfinder.view.adapter.RepoListAdapter
import com.freeman.hubfinder.viewmodel.RepoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@AndroidEntryPoint
class ItemListActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: RepoListViewModel
    lateinit var activityBinding: ActivityItemListBinding
    lateinit var repoListBinding: RepoListBinding

    @Inject
    lateinit var repoListAdapter: RepoListAdapter

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        repoListBinding = activityBinding.itemList.repoListLayout
        repoListBinding.repoList.layoutManager = LinearLayoutManager(this@ItemListActivity)
        repoListBinding.repoList.adapter = repoListAdapter

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            twoPane = true
        }

        repoListAdapter.setOnClickListeners(this, twoPane)
        setSearchOnQueryListener()
        setOnRefreshListener()

        viewModel = ViewModelProvider(this).get(RepoListViewModel::class.java)

        observeViewModel()
        viewModel.refresh()
    }

    fun observeViewModel() {
        viewModel.repoList.observe(this, Observer { repos ->
            repos?.let{
                repoListBinding.repoList.visibility = View.VISIBLE
                repoListAdapter.updateRepoList(it)
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                if(it) {
                    repoListBinding.loadingView.visibility = View.VISIBLE
                    repoListBinding.listError.visibility = View.GONE
                    repoListBinding.repoList.visibility = View.GONE
                } else {
                    repoListBinding.loadingView.visibility = View.GONE
                }
            }
        })

        viewModel.repoListLoadError.observe(this, Observer { isLoadError ->
            isLoadError?.let {
                    repoListBinding.loadingView.visibility = View.GONE
                    repoListBinding.repoList.visibility = View.GONE
                    repoListBinding.listError.visibility = View.VISIBLE
                    repoListBinding.listError.setText(getErrorStringId(it))
            } ?: run {
                repoListBinding.listError.visibility = View.GONE
            }
        })
    }

    fun setSearchOnQueryListener() {
        activityBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {
                viewModel.fetchRepos(qString)
                activityBinding.searchView.clearFocus()
                return true
            }
        })
    }

    fun setOnRefreshListener() {
        repoListBinding.itemList.setOnRefreshListener {
            repoListBinding.itemList.isRefreshing = false
            viewModel.refresh()
        }
    }
}