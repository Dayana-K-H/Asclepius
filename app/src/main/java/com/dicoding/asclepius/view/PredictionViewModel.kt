package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.PredictionRepository
import com.dicoding.asclepius.data.local.entity.PredictionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PredictionViewModel(private val predictionRepository: PredictionRepository) : ViewModel() {

    fun getAllPredictions() = predictionRepository.getAllPrediction()

    fun insertPrediction(predictionResult: PredictionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            predictionRepository.insert(predictionResult)
        }
    }

}