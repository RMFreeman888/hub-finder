package com.freeman.hubfinder.view.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.freeman.hubfinder.R
import com.freeman.hubfinder.model.GithubRepo
import com.freeman.hubfinder.util.loadImage
import com.freeman.hubfinder.view.ItemDetailActivity
import com.freeman.hubfinder.view.ItemDetailFragment

class RepoListAdapter(var githubRepos: ArrayList<GithubRepo>):
    RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    lateinit var onClickListener: View.OnClickListener

    fun setOnClickListeners(parentActivity: AppCompatActivity, twoPane: Boolean) {
        onClickListener = View.OnClickListener { view ->
            val item = view.tag as Long
            if(twoPane) {
                val fragment = ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ItemDetailFragment.ARG_ITEM_ID, item)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
            } else {
               val intent = Intent(view.context, ItemDetailActivity::class.java).apply {
                   putExtra(ItemDetailFragment.ARG_ITEM_ID, item)
               }
                view.context.startActivity(intent)
            }
        }
    }

    fun updateRepoList(newRepoList: List<GithubRepo>) {
        githubRepos.clear()
        githubRepos.addAll(newRepoList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepoViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_content, parent, false)
    )


    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = githubRepos[position]
        holder.bind(item)
        holder.itemView.setOnClickListener(onClickListener)
    }

    override fun getItemCount(): Int {
        return githubRepos.size
    }

    class RepoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val repoNameTextView: TextView = view.findViewById(R.id.repo_name)
        private val avatarImageView: ImageView = view.findViewById(R.id.avatar)

        fun bind(githubRepo: GithubRepo) {
            itemView.tag = githubRepo.id
            repoNameTextView.text = githubRepo.fullName
            avatarImageView.loadImage(githubRepo.owner.avatarUrl)
        }
    }
}