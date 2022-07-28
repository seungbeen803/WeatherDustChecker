package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.w3c.dom.Text
import java.net.URL

@JsonDeserialize(using = DustCheckResponseDeserializer::class)
data class DustCheckResponse(
    val pm10: Int,
    val pm25: Int,
    val pm10Status: String,
    val pm25Status: String)

// 역직렬화 클래스
class DustCheckResponseDeserializer : StdDeserializer<DustCheckResponse>(DustCheckResponse::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DustCheckResponse {
        // 로컬 함수 -> 메서드나 함수 안에 함수를 또 정의하는 것
        fun checkCategory(aqi: Int): String {
            return when(aqi) {
                in (0 ..100) -> "좋음"
                in (101 .. 200) -> "보통"
                in (201 .. 300) -> "나쁨"
                else -> "매우 나쁨"
            }
        }
        
        var node = p?.codec?.readTree<JsonNode>(p)
        var data = node?.get("data")
        var iaqi = data?.get("iaqi")
        var pm10 = iaqi?.get("pm10")?.get("v")?.asInt()
        var pm25 = iaqi?.get("pm25")?.get("v")?.asInt()


        return DustCheckResponse(pm10!!, pm25!!, checkCategory(pm10!!), checkCategory(pm25!!))
    }
}


class DustPageFragment : Fragment() {
    lateinit var statusImage : ImageView
    lateinit var pm25StatusText : TextView
    lateinit var pm25IntensityText : TextView
    lateinit var pm10StatusText : TextView
    lateinit var pm10IntensityText : TextView
    val APP_TOKEN = "62fb25ceb50a31c7cb4e1ba5d4cdb56742e15469"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.dust_page_fragment,container, false)

        statusImage = view.findViewById(R.id.dust_status_icon)
        pm25StatusText = view.findViewById(R.id.dust_pm25_status_text)
        pm25IntensityText = view.findViewById(R.id.dust_pm25_intensity_text)
        pm10StatusText = view.findViewById(R.id.dust_pm10_status_text)
        pm10IntensityText = view.findViewById(R.id.dust_pm10_intensity_text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val url = "https://api.waqi.info/feed/geo:${lat};${lon}/?token=${APP_TOKEN}"
        Log.d("mytag", url)
        APICall(object: APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("mytag", result)

                var mapper = jacksonObjectMapper()
                val data = mapper.readValue<DustCheckResponse>(result)
                
                Log.d("mytag", data.toString())

                statusImage.setImageResource(when(data.pm25Status) {
                    "좋음" -> R.drawable.good
                    "보통" -> R.drawable.normal
                    "나쁨" -> R.drawable.bad
                    else -> R.drawable.very_bad
                })
                pm25IntensityText.text = data.pm25?.toString()
                pm10IntensityText.text = data.pm10?.toString()
            }
        }).execute(URL(url))
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : DustPageFragment {
            val fragment = DustPageFragment()

            // 번들 객체에 필요한 정보를 저장
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }
}