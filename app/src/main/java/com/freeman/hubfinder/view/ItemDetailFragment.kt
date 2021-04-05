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
        Log.i("TESTDEBUG", "REACHED HERE")
        itemDetailBinding = ItemDetailBinding.inflate(layoutInflater, container, false)
        observeViewModel()
        viewModel.getRepositoryDetails(repoId)
//        var myWebView: WebView = itemDetailBinding.root.findViewById(R.id.readme_web_view)
//        myWebView.webViewClient = WebViewClient()
//        myWebView.loadUrl("https://google.com")
//        webView = itemDetailBinding.root.findViewById(R.id.readme_web_view)
//        webView.webViewClient = WebViewClient()
        return itemDetailBinding.root
    }

    fun observeViewModel() {
        viewModel.repoDetails.observe(viewLifecycleOwner, Observer { repoDetails ->
            repoDetails?.let{
//                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = repoDetails.name
                activity?.findViewById<ImageView>(R.id.backdrop)?.loadImage(repoDetails.owner.avatarUrl)
                itemDetailBinding.textUser.setText(repoDetails.owner?.login)
                itemDetailBinding.textFork.setText(repoDetails.forksCount.toString())
                itemDetailBinding.textWatching.setText(repoDetails.watchersCount.toString())
                itemDetailBinding.textLanguage.setText(repoDetails.language)
                itemDetailBinding.textIssues.setText(repoDetails.openIssues.toString())
                itemDetailBinding.textStar.setText(repoDetails.stargazersCount.toString())
            }
        })

        viewModel.readme.observe(viewLifecycleOwner, Observer { readme ->
            readme?.let{
                Log.i("TESTDEBUG", readme)
                itemDetailBinding.readmeWebView.loadUrl(readme)
                //webView.loadUrl(readme)
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