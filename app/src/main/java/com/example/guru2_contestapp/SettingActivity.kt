package com.example.guru2_contestapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SettingActivity : AppCompatActivity() {

    lateinit var tablayout : TabLayout
    lateinit var  viewPager2_1 : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_setting)+"</font>")
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)



        tablayout=findViewById<TabLayout>(R.id.tabLayout)
        viewPager2_1=findViewById<ViewPager2>(R.id.viewPager2_1)
        viewPager2_1.adapter =ViewPagerAdapter_setting(this)  // 어댑터 지정해주자.. 이거 안하면 오류남 ㅋㅋ
        CloseKeyboard()

        val tabLayoutTextArray = arrayOf("개인정보 수정","비밀번호 변경")


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


    fun CloseKeyboard()
    {
        var view = this.currentFocus

        if(view != null)
        {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}