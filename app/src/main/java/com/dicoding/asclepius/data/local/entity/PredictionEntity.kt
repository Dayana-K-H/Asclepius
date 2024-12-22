package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "imageUri")
    val imageUri: String,

    @ColumnInfo(name = "predictionResult")
    val predictionResult: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String = System.currentTimeMillis().toString()
)
