package com.freeman.hubfinder.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.freeman.hubfinder.R
import com.freeman.hubfinder.databinding.ItemDetailBinding
import com.freeman.hubfinder.util.getErrorStringId
import com.freeman.hubfinder.util.loadImage
import com.freeman.hubfinder.viewmodel.RepoListViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
@AndroidEntryPoint
class ItemDetailFragment : Fragment() {

    @Inject
    lateinit var viewModel: RepoListViewModel

    lateinit var itemDetailBinding: ItemDetailBinding
//    lateinit var webView: WebView
    private var repoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                repoId = it.getLong(ARG_ITEM_ID)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        itemDetailBinding = ItemDetailBinding.inflate(layoutInflater, container, false)
        observeViewModel()
        viewModel.getRepositoryDetails(repoId)

        itemDetailBinding.itemDetail.setOnRefreshListener {
            itemDetailBinding.itemDetail.isRefreshing = false
            viewModel.getRepositoryDetails(repoId)
        }

        return itemDetailBinding.root
    }

    fun observeViewModel() {
        viewModel.repoDetails.observe(viewLifecycleOwner, Observer { repoDetails ->
            repoDetails?.let{
                activity?.findViewById<ImageView>(R.id.backdrop)?.loadImage(repoDetails.owner.avatarUrl)
                itemDetailBinding.textUser.setText(repoDetails.owner.login)
                itemDetailBinding.textFork.setText(repoDetails.forksCount.toString())
                itemDetailBinding.textWatching.setText(repoDetails.watchersCount.toString())
                itemDetailBinding.textLanguage.setText(repoDetails.language)
                itemDetailBinding.textIssues.setText(repoDetails.openIssues.toString())
                itemDetailBinding.textStar.setText(repoDetails.stargazersCount.toString())
            }
        })

        viewModel.readme.observe(viewLifecycleOwner, Observer { readme ->
            readme?.let{
                itemDetailBinding.readmeWebView.visibility = View.VISIBLE
                itemDetailBinding.readmeWebView.loadUrl(readme)
            }
        })

        viewModel.isLoadingReadme.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let{
                if(isLoading) {
                    itemDetailBinding.readmeProgressSpinner.visibility = View.VISIBLE
                } else {
                    itemDetailBinding.readmeProgressSpinner.visibility = View.GONE
                }
            }
        })

        viewModel.readmeLoadingError.observe(this, Observer { isLoadError ->
            isLoadError?.let {
                itemDetailBinding.readmeWebView.visibility = View.GONE
                itemDetailBinding.readmeLoadError.visibility = View.VISIBLE
                itemDetailBinding.readmeLoadError.setText(getErrorStringId(it))
            } ?: run {
                itemDetailBinding.readmeLoadError.visibility = View.GONE
            }
        })

        viewModel.hasReadme.observe(this, Observer { hasReadme ->
            hasReadme?.let {
                if(!hasReadme) {
                    itemDetailBinding.readmeLoadError.visibility = View.VISIBLE
                    itemDetailBinding.readmeWebView.visibility = View.GONE
                    itemDetailBinding.readmeLoadError.setText(R.string.error_no_readme)
                } else {
                    itemDetailBinding.readmeLoadError.visibility = View.GONE
                }
            }
        })

    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}