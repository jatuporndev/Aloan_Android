package com.example.aloan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginBorrowerActivity : AppCompatActivity() {
    var back: TextView?=null
    var btnregis: TextView?=null
    var btnlogin: TextView?=null
    var txtemail:EditText?=null
    var txtpass:EditText?=null

    val appPreference:String = "appPrefer"
    val borrowerIdPreference:String = "BorrowerdPref"
    val emailPreference:String = "emailPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_borrower)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        back = findViewById(R.id.backlogin)
        btnregis=findViewById(R.id.btnregis)
        btnlogin=findViewById(R.id.btnlogin)
        txtemail=findViewById(R.id.txtemail)
        txtpass=findViewById(R.id.txtpass)
        back?.setOnClickListener {
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnregis?.setOnClickListener {
            val intent = Intent(applicationContext, RegisterBorrowerActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnlogin?.setOnClickListener {
            login()
        }
    }
    fun login(){
        val url = getString(R.string.root_url) + getString(R.string.loginBorrower_url)
        val okHttpClient = OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("email", txtemail?.text.toString())
            .add("password", txtpass?.text.toString())
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val obj = JSONObject(response.body!!.string())
                    val BorrowerID = obj["BorrowerID"].toString()
                    val email = obj["email"].toString()
                    val verify = obj["verify"].toString()

                    if (verify == "0"){
                        val builder1 = AlertDialog.Builder(this)
                        builder1.setMessage("อยู่ระหว่างตรวจสอบ")
                                .setCancelable(false)
                                .setNegativeButton("ตกลง") { dialog, _ ->
                                    dialog.cancel()
                                }
                        val alert11 = builder1.create()
                        alert11.setTitle("แจ้งเตือน")
                        alert11.show()
                    }else if (verify == "1"){

                        //Create shared preference to store user data
                        val sharedPrefer: SharedPreferences =
                            getSharedPreferences(appPreference, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPrefer.edit()

                        editor.putString(borrowerIdPreference, BorrowerID)
                        editor.putString(emailPreference, email)
                        editor.commit()

                        //return to login page
                        val intent = Intent(applicationContext, BorrowMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else if (verify == "2") {

                        val builder1 = AlertDialog.Builder(this)
                        builder1.setMessage("คุณไม่ผ่านการตรวจสอบ")
                                .setCancelable(false)
                                .setNegativeButton("ตกลง") { dialog, _ ->
                                    dialog.cancel()
                                }
                        val alert11 = builder1.create()
                        alert11.setTitle("แจ้งเตือน")
                        alert11.show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show()
                }
            } else {
                response.code
                Toast.makeText(applicationContext, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()

        }

    }
}