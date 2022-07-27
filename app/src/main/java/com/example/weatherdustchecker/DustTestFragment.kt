package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@JsonDeserialize(using=MyDeserializer::class)
data class DustJSONResponse(val pm10: Int, val pm25: Int)

class DustTestFragment : Fragment() {
    lateinit var dustImage : ImageView
    lateinit var dustTitle : TextView
    var APP_ID = "62fb25ceb50a31c7cb4e1ba5d4cdb56742e15469"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.dust_test_fragment, container, false)

        dustImage = view.findViewById<ImageView>(R.id.dust_icon)
        dustTitle = view.findViewById<TextView>(R.id.dust_title)

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

                dustTitle.text = data.pm10.toString()


            }
        })
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
