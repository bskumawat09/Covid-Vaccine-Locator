package com.example.covidvaccination

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covidvaccination.databinding.ActivityMainBinding
import com.example.covidvaccination.model.Center
import okhttp3.*
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private val BASE_URL =
        "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin"
    private lateinit var binding: ActivityMainBinding
    private var vaccineAdapter = CenterAdapter()
    private var centerList = arrayListOf<Center>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()

        binding.btnSearch.setOnClickListener {
            val pin = binding.etPincode.text.toString()
            if (pin.length != 6) {
                Toast.makeText(this, "Enter valid 6 digit pincode", Toast.LENGTH_SHORT).show()
            } else {
                val cal = Calendar.getInstance()
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMoth ->
                        binding.pbLoad.visibility = View.VISIBLE
                        val choosenDate = """${dayOfMoth}-${month + 1}-${year}"""

                        centerList.clear()
                        getResponse(pin, choosenDate)
                    },
                    year,
                    month,
                    day
                ).show()
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvCenter.apply {
            adapter = vaccineAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun getResponse(pin: String, date: String) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(BASE_URL + "?pincode=" + pin + "&date=" + date)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        extractJsonResponse(jsonResponse)
                    } catch (e: JSONException) {
                        Log.e("MainActivity", "Could not convert to JSONObject")
                        e.printStackTrace()
                    }
                }

                runOnUiThread {
                    binding.pbLoad.visibility = View.GONE
                    if (centerList.isEmpty()) {
                        binding.tvNodata.visibility = View.VISIBLE
                    } else {
                        binding.tvNodata.visibility = View.GONE
                    }
                    vaccineAdapter.update(centerList)
                }
            }
        })
    }

    fun extractJsonResponse(jsonResponse: JSONObject) {
        val jsonCentersArray = jsonResponse.getJSONArray("centers")
        val size: Int = jsonCentersArray.length()

        for (i in 0..size - 1) {
            val jsonCenter = jsonCentersArray.getJSONObject(i)
            Log.e("extractResponse()", jsonCenter.toString())

            val address = jsonCenter.getString("address")
            val name = jsonCenter.getString("name")
            val district = jsonCenter.getString("district_name")
            val state = jsonCenter.getString("state_name")
            val from = jsonCenter.getString("from")
            val to = jsonCenter.getString("to")
            val fees = jsonCenter.getString("fee_type")

            val sessionArray = jsonCenter.getJSONArray("sessions")
            val vaccine = sessionArray.getJSONObject(0).getString("vaccine")
            val ageLimit = sessionArray.getJSONObject(0).getString("min_age_limit").toInt()
            var available = 0
            for (j in 0..sessionArray.length() - 1) {
                available += sessionArray.getJSONObject(j).getString("available_capacity").toInt()
            }
            val availability = available

            val center = Center(
                address,
                district,
                fees,
                from,
                name,
                vaccine,
                state,
                to,
                ageLimit,
                availability
            )

            centerList.add(center)
        }
    }
}

/**Networking with Retrofit**/
//fun getData(pin: String, date: String) = MainScope().launch {
//    val response = RetrofitInstance.api.getResponse(pin, date)
//
//    if(response.isSuccessful) {
//        centersData = response as VaccinationCenter
//        binding.pbLoad.visibility = View.GONE
//        vaccineAdapter.update(centersData.centers)
//    }
//    else {
//        Log.e("MainActivity", "Could not fetched data.${response.raw()}")
//    }
//}