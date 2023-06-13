package com.example.weatherkt.roomdb
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherModel (

@PrimaryKey
@NonNull
val city: String="",


@ColumnInfo(name="lat")
val lat : String="",

@ColumnInfo(name="long")
val longi : String="",

@ColumnInfo(name="weather")
val weather : String="",

@ColumnInfo(name ="temp")
val temp :String="",

@ColumnInfo(name="mintemp")
val mintemp : String="",

@ColumnInfo(name="maxtemp")
val maxtemp : String="",


@ColumnInfo(name="pressure")
val pressure : String="",

@ColumnInfo(name="humidity")
val humidity : String="",

@ColumnInfo(name="speed")
val speed : String="",



@ColumnInfo(name="timestamp")
val timestamp: String=""

)