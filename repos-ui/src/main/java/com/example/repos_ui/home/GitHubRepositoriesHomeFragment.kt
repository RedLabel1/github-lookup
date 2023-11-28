package com.example.repos_ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repos_ui.R
import com.example.repos_ui.databinding.FragmentGithubRepositoriesHomeBinding
import com.example.repos_ui.home.adapter.GitHubRepositoriesAdapterListener
import com.example.repos_ui.home.adapter.GitHubRepositoriesHomeAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GitHubRepositoriesHomeFragment : Fragment(R.layout.fragment_github_repositories_home) {

    private val viewModel: GitHubRepositoriesHomeViewModel by hiltNavGraphViewModels(R.id.repos_ui_nav_graph)
    private var _binding: FragmentGithubRepositoriesHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGithubRepositoriesHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerAdapter = setupUI()
        observeViewModel(recyclerAdapter)
    }

    private fun setupUI(): GitHubRepositoriesHomeAdapter {
        with (binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            GitHubRepositoriesHomeAdapter(onAdapterItemClick).apply {
                adapter = this
                return this
            }
        }
    }

    private fun observeViewModel(recyclerAdapter: GitHubRepositoriesHomeAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.progressIndicator.isVisible = it.isLoading
                    if (it.error is NetworkError) {
                        it.error.message?.let { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    if (it.gitHubRepositories.isNotEmpty()) {
                        recyclerAdapter.submitList(it.gitHubRepositories.toMutableList())
                    }
                }
            }
        }
    }

    private val onAdapterItemClick = GitHubRepositoriesAdapterListener {
        viewModel.onItemSelected(it)
        navigateToDetail()
    }

    private fun navigateToDetail() {
        findNavController().navigate(R.id.action_gitHubRepositoriesHomeFragment_to_gitHubRepositoriesDetailFragment)
    }
}
