package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.PredictionRepository
import com.dicoding.asclepius.data.local.room.PredictionDatabase

object Injection {
    fun provideRepository(context: Context): PredictionRepository {
        val database = PredictionDatabase.getInstance(context)
        val predictionDao = database.predictionDao()
        return PredictionRepository.getInstance(predictionDao)
    }
}