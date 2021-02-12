package com.example.guru2_contestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchIdPwActivity : AppCompatActivity() {
    lateinit var searchTabLaout: TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_id_pw)
        searchTabLaout = findViewById(R.id.searchTabLayout)
        viewPager = findViewById(R.id.viewPager)

        viewPager.adapter = ViewPagerAdapter(this)

        val searchTabArray = arrayOf("아이디 찾기", "비밀번호 찾기")

        // TabLayoutMediator : tablayout과 viewPager 연결
        TabLayoutMediator(searchTabLaout, viewPager){tab,position->
            tab.text = searchTabArray[position]
            //   tab.setIcon(tabLayoutIconArray[position])
        }.attach()
    }

    var PAGE_CNT = 2
    private inner class ViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa){
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> SearchIdFragment()
                1 -> SearchPwFragment()
                else -> ErrorFragment()
            }
        }
        override fun getItemCount():Int = PAGE_CNT
    }
}