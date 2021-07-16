package com.example.aloan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoanerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaner_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, LoanerHomeFragment())
        transaction.commit()
        navView.setOnNavigationItemSelectedListener {
            var fm: Fragment = LoanerHomeFragment()
            when (it.itemId) {
                R.id.nav_home -> fm = LoanerHomeFragment()
                R.id.nav_notify -> fm = LoanerArticleFragment()
                R.id.nav_account-> fm = LoanerAccountFragment()

            }
            //this.supportActionBar!!.title = "Home"

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fm)
            transaction.commit()
            return@setOnNavigationItemSelectedListener true
        }
    }
}