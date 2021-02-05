package com.example.guru2_contestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Setting : AppCompatActivity() {

    lateinit var tablayout : TabLayout
    lateinit var  viewPager2_1 : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        tablayout=findViewById<TabLayout>(R.id.tabLayout)
        viewPager2_1=findViewById<ViewPager2>(R.id.viewPager2_1)
        viewPager2_1.adapter =ViewPagerAdapter_setting(this)  // 어댑터 지정해주자.. 이거 안하면 오류남 ㅋㅋ


        val tabLayoutTextArray = arrayOf("회원정보 수정","비밀번호 변경")


        // TabLayoutMediator : tablayout과 viewPager 연결
        TabLayoutMediator(tablayout,viewPager2_1){tab,position->
            tab.text = tabLayoutTextArray[position]
        }.attach()



        ////////아이템 선택
    }

    var PAGE_CNT=2
    // FragmentStateAdapter : Fragment 와 Viewpager연결
    private inner class ViewPagerAdapter_setting(fa: FragmentActivity): FragmentStateAdapter(fa){
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> MyInfoFragment()
                1 -> ChangePwFragment()
                else -> ErrorFragment()
            }
        }
        override fun getItemCount():Int = PAGE_CNT
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}