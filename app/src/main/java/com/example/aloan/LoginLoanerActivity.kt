package com.example.aloan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
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

class LoginLoanerActivity : AppCompatActivity() {
    var back: TextView?=null
    var btnregis: TextView?=null
    var btnlogin: TextView?=null
    var txtemail:EditText?=null
    var txtpass:EditText?=null

    val appPreference:String = "appPrefer"
    val LoanerIdPreference:String = "LoanerIdPref"
    val emailPreference:String = "emailPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_loaner)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        back = findViewById(R.id.backlogin)
        btnregis=findViewById(R.id.txtregis)
        btnlogin=findViewById(R.id.btnlogin)
        txtemail=findViewById(R.id.txtemail)
        txtpass=findViewById(R.id.txtpass)

        back?.setOnClickListener {
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnregis?.setOnClickListener {
            val intent = Intent(applicationContext, RegisterLoanerActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnlogin?.setOnClickListener {
            login()
        }
    }

    fun login(){
        val url = getString(R.string.root_url) + getString(R.string.loginLoaner_url)
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
                    val loanerID = obj["LoanerID"].toString()
                    val username = obj["email"].toString()
                    val verify = obj["verify"].toString()
                    val setborrowlist = obj["setborrowlist"].toString()

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

                        if (setborrowlist=="0"){//สร้างลิส ถ้ามีอยู่แล้วก็ข้ามไป
                            addBorrowlist(loanerID)
                            setborrowlist(loanerID)
                        }
                    //Create shared preference to store user data
                    val sharedPrefer: SharedPreferences =
                            getSharedPreferences(appPreference, Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPrefer.edit()

                    editor.putString(LoanerIdPreference, loanerID)
                    editor.putString(emailPreference, username)
                    editor.commit()

                    //return to login page
                        val intent = Intent(applicationContext, LoanerMainActivity::class.java)
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
    fun addBorrowlist(id:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_borrowlist_url)+id
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .build()

        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun setborrowlist(id:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_setborrowlist_url)+id
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .build()

        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}