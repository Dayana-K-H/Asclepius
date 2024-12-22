package com.dicoding.asclepius.view.history

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.PredictionAdapter
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.di.Injection
import com.dicoding.asclepius.view.PredictionViewModel
import com.dicoding.asclepius.view.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var predictionViewModel: PredictionViewModel
    private lateinit var adapter: PredictionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PredictionAdapter()
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        val repository = Injection.provideRepository(this)
        val factory = ViewModelFactory(repository)
        predictionViewModel = ViewModelProvider(this, factory)[PredictionViewModel::class.java]


        predictionViewModel.getAllPredictions().observe(this) { historyList ->
            Log.d("HistoryActivity", "History List: ${historyList.size}")
            binding.tvNoHistory.visibility = if (historyList.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHistory.visibility = if (historyList.isNotEmpty()) View.VISIBLE else View.GONE

            if (historyList.isNotEmpty()) {
                adapter.submitList(historyList)
            }
        }

    }
}
