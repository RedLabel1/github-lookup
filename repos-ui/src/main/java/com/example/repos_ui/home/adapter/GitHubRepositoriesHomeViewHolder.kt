package com.example.repos_ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.repos_ui.databinding.ItemGithubRepoBinding
import com.example.repos_ui.model.GitHubRepositoryUIModel

class GitHubRepositoriesHomeViewHolder(private val binding: ItemGithubRepoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(viewGroup: ViewGroup): GitHubRepositoriesHomeViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemGithubRepoBinding.inflate(layoutInflater, viewGroup, false)
            return GitHubRepositoriesHomeViewHolder(binding)
        }
    }

    fun bind(uiModel: GitHubRepositoryUIModel, listener: GitHubRepositoriesAdapterListener) {
        binding.apply {
            name.text = uiModel.name
            description.text = uiModel.description
            stars.text = uiModel.stars?.toString()
            this.root.setOnClickListener {
                listener.onClick(uiModel)
            }
            Glide.with(binding.root).load(uiModel.ownerAvatar).into(avatar)
        }
    }
}
