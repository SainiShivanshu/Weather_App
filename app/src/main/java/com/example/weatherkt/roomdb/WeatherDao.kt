package com.example.weatherkt.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface WeatherDao {
    @Insert
    suspend fun insertWeather(weather:WeatherModel)

    @Delete
    suspend fun deleteWeather(weather: WeatherModel)

    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()

    @Query("SELECT * FROM weather")
    fun getAllWeather() : LiveData<List<WeatherModel>>


}


