package com.example.weatherkt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.weatherkt.Adapter.weatherAdapter
import com.example.weatherkt.model.weaModel
import com.example.weatherkt.roomdb.AppDatabase
import com.example.weatherkt.roomdb.WeatherDao
import com.example.weatherkt.roomdb.WeatherModel
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    var myRequestCode=1010;
    lateinit var mfusedlocation: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


         loadData()
        loadrecyclerdata()


    current_location.setOnClickListener {
        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

        restart.setOnClickListener {
            getWeatherInfo(city.text.toString())
        }

        search.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val city = cityEdt.text.toString()
                if (city.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Please enter city Name", Toast.LENGTH_SHORT)
                        .show()
                } else {
        getWeatherInfo(city)
                }
            }
        })
    }

    private fun loadrecyclerdata() {

        val cities= arrayOf("New York" , "Singapore", "Mumbai" , "Delhi" , "Sydney" , "Melbourne")

        val data = ArrayList<weaModel>()
data.clear()
        for(city in cities){
            val API_KEY = "d0f056b51d4becc728d4a078dd2d1002"
            val queue = Volley.newRequestQueue(this)
            val url = "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${API_KEY}"
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET, url,null,
                Response.Listener { response ->
                    val c =response?.getString("name").toString()
                    var tempr=response?.getJSONObject("main")?.getString("temp")
                    tempr=((((tempr)?.toFloat()!!-273.15)).toInt()).toString()
                    val temp = "${tempr}°C"

                    val desp=response?.getJSONArray("weather")?.getJSONObject(0)?.getString("main").toString()

                    val a=weaModel(c,temp,desp)
                    data.add(a)

                },
                Response.ErrorListener { Toast.makeText(this,"Not able to get weather",Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
                   queue.add(jsonRequest)
             }
        val adapter = weatherAdapter(this, data)

        recycler.adapter=adapter

        adapter.notifyDataSetChanged()

    }

    private fun loadData() {

            val dao = AppDatabase.getInstance(this).weatherDao()

            dao.getAllWeather().observe(this){ existing ->
                if (existing.isNotEmpty()) {
                    val data = existing[0]

                    city.text = data.city
                    coordinates.text = "${data.lat} , ${data.longi}"
                    weather.text = data.weather
                    temp.text = data.temp
                    min_temp.text = data.mintemp
                    max_temp.text = data.maxtemp
                    pressure.text = data.pressure
                    wind.text = data.speed
                    humidity.text = data.humidity
                    timeStamp.text=data.timestamp
                }
                else{
                    mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
                    getLastLocation()
                }


            }
    }

    private fun getWeatherInfo(city: String) {
        val API_KEY = "d0f056b51d4becc728d4a078dd2d1002"
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${API_KEY}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this,"Not able to get weather",Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
        queue.add(jsonRequest)

    }

    private fun getJsonData(lat: String?, long: String?) {
val API_KEY = "d0f056b51d4becc728d4a078dd2d1002"
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${API_KEY}"


        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this,"Not able to get current location",Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }

    private fun setValues(response: JSONObject?) {
        city.text=response?.getString("name")


                var weatherDao =AppDatabase.getInstance(this).weatherDao()


//                var code = response?.getJSONArray("weather")?.getJSONObject(0)?.getString("icon")
//                var url ="https://openweathermap.org/img/wn/${code}@2x.png"
//
//                Glide.with(this).load(url).into(image)


                var lat = response?.getJSONObject("coord")?.getString("lat")+"°"
                var longi=response?.getJSONObject("coord")?.getString("lon")+"°"
                coordinates.text="${lat} , ${longi}"
                weather.text=response?.getJSONArray("weather")?.getJSONObject(0)?.getString("main")
                var tempr=response?.getJSONObject("main")?.getString("temp")
                tempr=((((tempr)?.toFloat()!!-273.15)).toInt()).toString()
                temp.text="${tempr}°C"


                var mintemp=response?.getJSONObject("main")?.getString("temp_min")
                mintemp=((((mintemp)?.toFloat()!!-273.15)).toInt()).toString()
                min_temp.text=mintemp+"°C"+" -"
                var maxtemp=response?.getJSONObject("main")?.getString("temp_max")
                maxtemp=((ceil((maxtemp)?.toFloat()!! -273.15)).toInt()).toString()
                max_temp.text=maxtemp+"°C"

                pressure.text=response?.getJSONObject("main")?.getString("pressure")+" hPa"
                humidity.text=response?.getJSONObject("main")?.getString("humidity")+"%"
                wind.text=response?.getJSONObject("wind")?.getString("speed")+" m/s"
      //          degree.text="Degree : "+response?.getJSONObject("wind")?.getString("deg")+"°"
//                gust.text="Gust : "+response?.getJSONObject("wind")?.getString("gust")+"°"


                val calendar = Calendar.getInstance().time
                val current = DateFormat.getInstance().format(calendar)
timeStamp.text=current.toString()

                val data=WeatherModel(city=city.text.toString(), lat = lat.toString(),
                    longi = longi.toString(),   weather=  weather.text.toString(), temp =temp.text.toString(),
                    mintemp = min_temp.text.toString(), maxtemp= max_temp.text.toString(), pressure=pressure.text.toString(),
                    humidity = humidity.text.toString(),
                speed=wind.text.toString(), timestamp = current.toString())



                GlobalScope.launch(Dispatchers.IO){

                    val existing = weatherDao.getAllWeather()
                    if(existing!=null){
                        weatherDao.deleteAllWeather()
                    }

                    weatherDao.insertWeather(data)
                }


    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
             R.id.About-> {

                var intent = Intent(this, AboutUs::class.java)

                startActivity(intent)
            }
             }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(chkPermission()){
            if(LocationEnable()){

                mfusedlocation.lastLocation.addOnCompleteListener{

                        task->
                    var location : Location? = task.result
                    if(location==null){
                        NewLocation()
                    }
                    else{
                        getJsonData(location.latitude.toString(),location.longitude.toString())

                    }
                }
            }
            else{
                Toast.makeText(this,"please Turn on your GPS location",Toast.LENGTH_LONG).show()

            }
        }
        else{
            RequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun NewLocation() {
        var locationRequest= LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=1
        mfusedlocation=LocationServices.getFusedLocationProviderClient(this)
        mfusedlocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }

    private val locationCallback= object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location? =p0.lastLocation
        }
    }

    private fun LocationEnable(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun RequestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION ),myRequestCode)
    }

    private fun chkPermission(): Boolean {
        if(
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED||
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED

        ){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==myRequestCode){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }

}

