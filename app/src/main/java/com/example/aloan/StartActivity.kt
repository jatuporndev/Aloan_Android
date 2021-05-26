package com.example.aloan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button

class StartActivity : AppCompatActivity() {
    var btnLoaner :Button?=null
    var btnBorrower:Button?=null

    var LoanerID=""
    var BorrowerID=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        btnBorrower = findViewById(R.id.btnborrower)
        btnLoaner =findViewById(R.id.btnloaner)

        btnBorrower?.setOnClickListener {
            val intent = Intent(applicationContext, LoginBorrowerActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLoaner?.setOnClickListener {

            val intent = Intent(applicationContext, LoginLoanerActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {

        val sharedPrefer = getSharedPreferences(
                LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        LoanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null).toString()

        val sharedPrefer2 = getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        BorrowerID = sharedPrefer2?.getString(LoginBorrowerActivity().borrowerIdPreference, null).toString()

        //if (sharedPrefer.contains(usernamePreference))
        if (LoanerID != "null") {
            val i = Intent(this, LoanerMainActivity::class.java)
            startActivity(i)
            finish()
        }
        if (BorrowerID != "null") {
            val i = Intent(this, BorrowMainActivity::class.java)
            startActivity(i)
            finish()
        }


        super.onResume()
    }
}