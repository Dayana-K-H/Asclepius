package com.dicoding.asclepius.view.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.FragmentNewsBinding
import com.google.android.material.snackbar.Snackbar

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<NewsViewModel>()

    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NewsAdapter().apply {
            setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ArticlesItem) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    startActivity(intent)
                }
            })
        }

        binding.rvNews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNews.adapter = adapter

        mainViewModel.articles.observe(viewLifecycleOwner) { listNews ->
            setEventData(listNews)
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        mainViewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { snackbarText ->
                Snackbar.make(requireView(), snackbarText, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvNoData.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun setEventData(listNews: List<ArticlesItem>) {
        binding.tvNoData.visibility = if (listNews.isEmpty()) View.VISIBLE else View.GONE
        binding.rvNews.visibility = if (listNews.isNotEmpty()) View.VISIBLE else View.GONE

        if (listNews.isNotEmpty()) {
            adapter.submitList(listNews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}