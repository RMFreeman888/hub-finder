package com.freeman.hubfinder.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freeman.hubfinder.R
import com.freeman.hubfinder.model.GithubRepo
import com.freeman.hubfinder.util.loadImage

class RepoListAdapter(var githubRepos: ArrayList<GithubRepo>):
    RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

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
        holder.bind(githubRepos[position])
    }

    override fun getItemCount(): Int {
        return githubRepos.size
    }

    class RepoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val repoNameTextView: TextView = view.findViewById(R.id.repo_name)
        private val avatarImageView: ImageView = view.findViewById(R.id.avatar)
        fun bind(githubRepo: GithubRepo) {
            repoNameTextView.text = githubRepo.repoName
            Log.i("TESTDEBUG", githubRepo.owner?.avatarUrl)
            avatarImageView.loadImage(githubRepo.owner?.avatarUrl)
        }
    }
}