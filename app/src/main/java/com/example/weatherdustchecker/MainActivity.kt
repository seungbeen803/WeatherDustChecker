package com.example.weatherdustchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat: Double = 37.579876
    private var lon: Double = 126.976998

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 상단 제목 표시줄 숨기기
        supportActionBar?.hide()

        mPager = findViewById(R.id.pager)
        // supportFragmentManager -> 이 activity에서 fragment를 관리하는 역할
        val pagerAdapter = Mypageradpter(supportFragmentManager, lat, lon)
        mPager.adapter = pagerAdapter

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

    // FragmentStatePagerAdapter -> 상속을 받음
    // 실제 일은 상속을 받는 것이 한다
    // 주생성자로 넘어오지만 속성은 아님 스쳐지나간다
    // class 앞에 inner를 써도 되지만 속성을 사용해도 된다
    class Mypageradpter(fm: FragmentManager, val lat: Double, val lon: Double) : FragmentStatePagerAdapter(fm) {
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