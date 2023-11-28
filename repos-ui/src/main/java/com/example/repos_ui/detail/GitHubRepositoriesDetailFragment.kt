package com.example.repos_ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.repos_ui.R
import com.example.repos_ui.databinding.FragmentGithubRepositoriesDetailBinding
import com.example.repos_ui.home.GitHubRepositoriesHomeViewModel
import com.example.repos_ui.home.NetworkError
import com.example.repos_ui.model.GitHubRepositoryUIModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GitHubRepositoriesDetailFragment : Fragment(R.layout.fragment_github_repositories_detail) {

    private val viewModel: GitHubRepositoriesHomeViewModel by hiltNavGraphViewModels(R.id.repos_ui_nav_graph)
    private var _binding: FragmentGithubRepositoriesDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGithubRepositoriesDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.fetchSelectedRepositoryTopLanguage()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (it.error is NetworkError) {
                        it.error.message?.let { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    if (it.selectedRepository != null) {
                        displayAllValues(it.selectedRepository)
                    }
                }
            }
        }
    }

    private fun displayAllValues(selectedRepository: GitHubRepositoryUIModel) {
        with(binding) {
            Glide.with(root).load(selectedRepository.ownerAvatar).into(avatar)
            name.text = selectedRepository.name
            description.text = selectedRepository.description
            stars.text = selectedRepository.stars.toString()
            forks.text = selectedRepository.forks.toString()
            primaryLanguage.text = selectedRepository.language
        }
    }
}
