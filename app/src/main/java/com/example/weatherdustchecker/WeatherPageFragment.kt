package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

@JsonDeserialize(using=MyDeserializer::class)
data class OpenWeatherApIJSONResponse(val temp: Double, val id: Int)

// 상속 받아 <> -> 타입만 바꿔줌
class MyDeserializer : StdDeserializer<OpenWeatherApIJSONResponse>(
    OpenWeatherApIJSONResponse::class.java
){
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): OpenWeatherApIJSONResponse {
        val node = p?.codec?.readTree<JsonNode>(p)

        val weather = node?.get("weather")
        val firstWeather = weather?.elements()?.next()
        val id =  firstWeather?.get("id")?.asInt()
        val main = node?.get("main")
        val temp = main?.get("temp")?.asDouble()

        //권장하지는 않음 but 한 줄로 작성 가능하다
//      val id = node?.get("weather")?.elements()?.next()?.get("id")?.asInt()
//      val temp = node?.get("main")?.get("temp")?.asDouble()
        // null일 수도 있기 때문에 강제 형변환을 해줌
        return OpenWeatherApIJSONResponse(temp!!, id!!)

    }

}

class WeatherPageFragment : Fragment() {
    lateinit var weatherImage : ImageView
    lateinit var statusText : TextView
    lateinit var temperatureText : TextView
    var APP_ID = "5321b3d405765afa8579e45daa11a457"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.weather_page_fragment,container, false)

        // TODO : arguments 값 참조해서 두 개 값 가져오고, 해당하는 뷰에 출력하기
        statusText = view.findViewById<TextView>(R.id.weather_status_text)
        temperatureText = view.findViewById<TextView>(R.id.weather_temp_text)
        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)

//        statuesText.text = arguments?.getString("status")
//        temperatureText.text = arguments?.getDouble("temperature").toString()
        // TODO : ImageView 가져와서 sun 이미지 출력
//        weatherImage.setImageResource(arguments?.getInt("res_id")!!)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        var url = "https://api.openweathermap.org/data/2.5/weather?units=metric&appid=${APP_ID}&lat=${lat}&lon=${lon}"
        Log.d("mytag", url)

//        APICall(object: APICall.APICallback {
//            override fun onComplete(result: String) {
////                Log.d("mytag", result)
//                var mapper = jacksonObjectMapper()
//                var data = mapper?.readValue<OpenWeatherApIJSONResponse>(result)
//
//                temperatureText.text = data.temp.toString()
//                val id = data.id.toString()
//                if(id != null) {
//                    statusText.text = when {
//                        id.startsWith("2") -> {
//                            weatherImage.setImageResource(R.drawable.flash)
//                            "천둥, 번개"
//                        }
//                        id.startsWith("3") -> {
//                            weatherImage.setImageResource(R.drawable.rain)
//                            "이슬비"
//                        }
//                        id.startsWith("5") -> {
//                            weatherImage.setImageResource(R.drawable.rain)
//                            "비"
//                        }
//                        id.startsWith("6") -> {
//                            weatherImage.setImageResource(R.drawable.snow)
//                            "눈"
//                        }
//                        id.startsWith("7") -> {
//                            weatherImage.setImageResource(R.drawable.cloudy)
//                            "흐림"
//                        }
//                        id.equals("800") -> {
//                            weatherImage.setImageResource(R.drawable.sun)
//                            "화창"
//                        }
//                        id.startsWith("8") -> {
//                            weatherImage.setImageResource(R.drawable.cloud)
//                            "구름 낌"
//                        }
//                        else -> "알 수 없음"
//                    }
//                }
//
//            }
//        }).execute(URL(url))

        // ** 구현에 관심있다면 주의 깊게 보기
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org")
            // 선언을 하면 역직렬화 할 때에 Gson을 사용함 (역직렬화 방법)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(WeatherAPIService::class.java)
        // 호출할 수 있는 준비를 끝냄
        val apiCallForData = apiService.getWeatherStatusInfo(APP_ID, lat!!, lon!!)

        // callback() 함수를 받아서 호출하는 방법이 있다

        apiCallForData.enqueue(object : Callback<OpenWeatherAPIJSONResponseGSON> {
            override fun onResponse(
                call: Call<OpenWeatherAPIJSONResponseGSON>,
                response: Response<OpenWeatherAPIJSONResponseGSON>
            ) {
                val data = response.body()
                Log.d("mytag", data.toString())
                
                // TODO : 다시 날씨 화면 나오도록 코드 작성


                temperatureText.text = data?.main?.get("temp")
                // 리스트는 방 번호 즉 인덱스 번호를 이용해서 가져온다
                val id = data?.weather?.get(0)?.get("id")
                    if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }

            }
            override fun onFailure(call: Call<OpenWeatherAPIJSONResponseGSON>, t: Throwable) {
                Toast.makeText(activity,
                "에러 발생 : ${t.message}",
                Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : WeatherPageFragment {
            val fragment = WeatherPageFragment()

            // 번들 객체에 필요한 정보를 저장
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }

   

}