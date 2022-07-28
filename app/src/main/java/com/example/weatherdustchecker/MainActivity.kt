package com.example.weatherdustchecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    val PERMISSION_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치와 관련된 것을 관리 -> 위치 정보를 얻기 위해서 필요
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        // 위치가 바뀔 때 실행된다
        locationListener = LocationListener {
            lat = it.latitude
            lon = it.longitude
            Log.d("mytag", lat.toString())
            Log.d("mytag", lon.toString())

            locationManager.removeUpdates(locationListener)

            val pagerAdapter = MyPagerAdpter(supportFragmentManager, lat, lon)
            mPager.adapter = pagerAdapter


        }

        // FINE_LOCATION, COARSE_LOCATION에 대한 권한을 사용자로부터 허용을 받았는지의 여부를 의미
        // 권한을 획득했는지 안했는지 알려주는 코드
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
            // 
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            // PERMISSION_GRANTED에서 true가 나오면 위치 정보 요청을 허용을 했다는 의미
            == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener)
        }else{
            // 허가해 달라는 요청 창을 띄움
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE)
        }


        // 상단 제목 표시줄 숨기기
        supportActionBar?.hide()

        mPager = findViewById(R.id.pager)
        // supportFragmentManager -> 이 activity에서 fragment를 관리하는 역할

        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
            // 페이지 이동이 끝났을 때 실행
            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    Toast.makeText(applicationContext,
                    "날씨 페이지입니다.",
                    Toast.LENGTH_SHORT).show()
                }else if(position == 1) {
                    Toast.makeText(application,
                    "미세먼지 페이지입니다.",
                    Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}


        })
        
        // TODO : WeatherPageFragment 표시하기 (FrameLayout에 넣어주기)
        // val transaction = supportFragmentManager.beginTransaction()
        // TODO : newInstance 클래스 메서드 정의해서 status값(문자열),
        // temperature값(Double) 전달할 수 있도록 해주기
        // 해당 값은 모두 프래그먼트의 번들 객체에 저장되어야 함
        // transaction.add(R.id.fragment_container, WeatherPageFragment.newInstance(37.58, 126.98))
        // transaction.add(R.id.fragment_container, DustTestFragment.newInstance(37.58, 126.98))
//        transaction.add(R.id.fragment_container, DustPageFragment.newInstance(37.58, 126.98))
//        transaction.commit()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 요청을 보내는 코드를 경우에 따라 나누는 작업
        if(requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
                if(!allPermissionsGranted) break
            }
            // 위치정보를 받겠다는 의미
            if(allPermissionsGranted) {
                // 원래는 NETWORK 로 작성해야한다 ->  단말기에서 작동할 때는
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }else{
                Toast.makeText(applicationContext,
                    "위치 정보 제공 동의가 필요합니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // FragmentStatePagerAdapter -> 상속을 받음
    // 실제 일은 상속을 받는 것이 한다
    // 주생성자로 넘어오지만 속성은 아님 스쳐지나간다
    // class 앞에 inner를 써도 되지만 속성을 사용해도 된다
    class MyPagerAdpter(fm: FragmentManager, val lat: Double, val lon: Double) : FragmentStatePagerAdapter(fm) {
        // 짧게 return 가능
        override fun getCount() = 2
        override fun getItem(position: Int): Fragment {
            return  when(position) {
                0 -> WeatherPageFragment.newInstance(lat, lon)
                1 -> DustPageFragment.newInstance(lat, lon)
                // 예외처리
                else -> throw Exception("페이지가 존재하지 않음")
            }
        }

    }
}