package com.example.repos_ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.repos_ui.model.GitHubRepositoryUIModel

class GitHubRepositoriesHomeAdapter(
    private val listener: GitHubRepositoriesAdapterListener,
    callback: Callback = Callback()
) : ListAdapter<GitHubRepositoryUIModel, GitHubRepositoriesHomeViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubRepositoriesHomeViewHolder =
        GitHubRepositoriesHomeViewHolder.from(parent)

    override fun onBindViewHolder(holder: GitHubRepositoriesHomeViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class Callback : DiffUtil.ItemCallback<GitHubRepositoryUIModel>() {
        override fun areItemsTheSame(oldItem: GitHubRepositoryUIModel, newItem: GitHubRepositoryUIModel): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.description == newItem.description
                    && oldItem.stars == newItem.stars
        }

        override fun areContentsTheSame(oldItem: GitHubRepositoryUIModel, newItem: GitHubRepositoryUIModel): Boolean {
            return oldItem == newItem
        }
    }
}

class GitHubRepositoriesAdapterListener(val listener: (model: GitHubRepositoryUIModel) -> Unit) {

    fun onClick(model: GitHubRepositoryUIModel) = listener(model)
}
