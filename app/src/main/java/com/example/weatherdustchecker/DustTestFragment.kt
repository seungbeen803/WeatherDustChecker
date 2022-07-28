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
import java.net.URL

@JsonDeserialize(using=MyDust::class)
data class DustJSONResponse(val pm10: Int, val pm25: Int)

class MyDust : StdDeserializer<DustJSONResponse>(
    DustJSONResponse::class.java
){
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): DustJSONResponse {
        val node = p?.codec?.readTree<JsonNode>(p)

        val data = node?.get("data")
        val iaqi = data?.get("iaqi")
        val pm10 = iaqi?.get("pm10")?.get("v")?.asInt()
        val pm25 = iaqi?.get("pm25")?.get("v")?.asInt()

        return DustJSONResponse(pm10!!, pm25!!)
    }

}

class DustTestFragment : Fragment() {
    lateinit var dustImage : ImageView
    lateinit var dustPm10Text : TextView
    lateinit var dustPm10Title : TextView
    lateinit var dustPm25Text : TextView
    lateinit var dustPm25Title : TextView
    var APP_ID = "62fb25ceb50a31c7cb4e1ba5d4cdb56742e15469"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.dust_test_fragment, container, false)

        dustImage = view.findViewById<ImageView>(R.id.dust_icon)
        dustPm10Title = view.findViewById(R.id.dust_pm10_title)
        dustPm10Text = view.findViewById(R.id.dust_pm10)
        dustPm25Title = view.findViewById(R.id.dust_pm25_title)
        dustPm25Text = view.findViewById(R.id.dust_pm25)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        var url = "https://api.waqi.info/feed/geo:${lat};${lon}/?token=${APP_ID}"

        APICall(object: APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("mytag", result)
                var mapper = jacksonObjectMapper()
                var data = mapper?.readValue<DustJSONResponse>(result)

                dustPm10Text.text = data.pm10.toString()
                dustPm25Text.text = data.pm25.toString()
                if (data.pm25 != null) {
                    dustPm25Title.text= when {
                        data.pm25 <= 50 -> {
                            dustImage.setImageResource(R.drawable.good)
                            "좋음(초미세먼지)"
                        }
                        data.pm25 in 51..150 -> {
                            dustImage.setImageResource(R.drawable.normal)
                            "보통(초미세먼지)"
                        }
                        data.pm25 in 151..300 -> {
                            dustImage.setImageResource(R.drawable.bad)
                            "나쁨(초미세먼지)"
                        }
                        else -> {
                        dustImage.setImageResource(R.drawable.very_bad)
                            "매우 나쁨(초미세먼지)"
                        }
                    }
                }

                if (data.pm10 != null) {
                    dustPm10Title.text= when {
                        data.pm10 <= 50 -> {
                            dustImage.setImageResource(R.drawable.good)
                            "좋음(초미세먼지)"
                        }
                        data.pm10 in 51..150 -> {
                            dustImage.setImageResource(R.drawable.normal)
                            "보통(초미세먼지)"
                        }
                        data.pm10 in 151..300 -> {
                            dustImage.setImageResource(R.drawable.bad)
                            "나쁨(초미세먼지)"
                        }
                        else -> {
                            dustImage.setImageResource(R.drawable.very_bad)
                            "매우 나쁨(초미세먼지)"
                        }
                    }
                }

            }
        }).execute(URL(url))
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : DustTestFragment {
            val fragment = DustTestFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }

}
