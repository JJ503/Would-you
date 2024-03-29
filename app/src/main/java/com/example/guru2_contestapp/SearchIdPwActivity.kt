package com.example.guru2_contestapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchIdPwActivity : AppCompatActivity() {
    lateinit var searchIdPw: ConstraintLayout
    lateinit var searchTabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_id_pw)

        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_search)+"</font>"))

        searchIdPw = findViewById(R.id.JsearchIdPw)
        searchTabLayout = findViewById(R.id.searchTabLayout)
        viewPager = findViewById(R.id.viewPager)

        searchIdPw.setOnClickListener {
            CloseKeyboard()
        }

        viewPager.adapter = ViewPagerAdapter(this)

        val searchTabArray = arrayOf("아이디 찾기", "비밀번호 찾기")

        // TabLayoutMediator : tablayout과 viewPager 연결
        TabLayoutMediator(searchTabLayout, viewPager){tab,position->
            tab.text = searchTabArray[position]
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