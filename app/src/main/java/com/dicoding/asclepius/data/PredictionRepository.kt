package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.PredictionEntity
import com.dicoding.asclepius.data.local.room.PredictionDao

class PredictionRepository (
    private val predictionDao: PredictionDao
) {

    fun getAllPrediction(): LiveData<List<PredictionEntity>> {
        return predictionDao.getAllPrediction()
    }

    fun insert(prediction: PredictionEntity) {
        return predictionDao.insert(prediction)
    }

    companion object {
        @Volatile
        private var instance: PredictionRepository? = null
        fun getInstance(
            predictionDao: PredictionDao,
        ): PredictionRepository =
            instance ?: synchronized(this) {
                instance ?: PredictionRepository(predictionDao)
            }.also { instance = it }
    }
}
