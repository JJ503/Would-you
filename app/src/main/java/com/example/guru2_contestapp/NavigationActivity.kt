package com.example.guru2_contestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    private val fragmentHome by lazy { HomeFragment() }
    //private val fragmentContest by lazy { ContestFragment() }
    //private val fragmentTeam by lazy { TeamFragment() }
    //private val fragmentPersonal by lazy { PersonalFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

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
                    /*R.id.groupMenu -> {
                        changeFragment(fragmentContest)
                    }
                    R.id.searchMenu -> {
                        changeFragment(fragmentTeam)
                    }*/
                    /*R.id.personalMenu -> {
                        changeFragment(fragmentPersonal)
                    }*/
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