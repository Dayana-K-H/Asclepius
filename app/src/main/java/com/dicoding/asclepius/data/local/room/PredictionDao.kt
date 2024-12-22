package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.asclepius.data.local.entity.PredictionEntity

@Dao
interface PredictionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(prediction: PredictionEntity)

    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC")
    fun getAllPrediction(): LiveData<List<PredictionEntity>>
}