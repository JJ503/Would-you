package com.example.guru2_contestapp


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentContest by lazy { ContestFragment() }
    private val fragmentTeam by lazy { TeamFragment() }
    private val fragmentPersonal by lazy { PersonalFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name)+"</font>")
        supportActionBar?.setIcon(R.drawable.logo_2_04)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bottomNavigation=findViewById(R.id.WbottomNavigationView)
        initNavigationBar()
    }

    private fun initNavigationBar() {
        bottomNavigation.run {
            setOnNavigationItemSelectedListener {

                when(it.itemId){
                    R.id.homeMenu -> {
                        changeFragment(fragmentHome)
                    }
                    R.id.contestMenu -> {
                        changeFragment(fragmentContest)
                    }
                    R.id.teamMenu -> {
                        changeFragment(fragmentTeam)
                    }
                    R.id.personalMenu -> {
                        changeFragment(fragmentPersonal)
                    }
                }
                true
            }

            selectedItemId=R.id.homeMenu
        }
    }

    private fun changeFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, fragment)
            .commit()
    }
}