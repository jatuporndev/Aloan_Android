package com.example.aloan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class  LoanerAccountFragment : Fragment() {
    var btnlogout : Button? = null

    var txtcoutRequest:TextView?=null//1
    var txtcoutpay:TextView?=null//2
    var txtcoutWaitpay:TextView?=null//3

    var loanerID: String? = null
    var txtname:TextView?=null
    var txtphone:TextView?=null
    var txtemail:TextView?=null
    var txtLineID:TextView?=null
    var imgpro:ImageView?=null

    var btnrequest:ImageButton?=null//1
    var btnpay:ImageButton?=null//2
    var btnWaitPay:ImageButton?=null//3

    var btneditbank:Button?=null//เมนู1
    var btneditprofile:Button?=null//เมนู2


    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loaner_account, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        btnlogout =root.findViewById(R.id.btnlogout)
        txtcoutRequest=root.findViewById(R.id.txtcountrequest)
        txtcoutpay=root.findViewById(R.id.txtcountpay)
        txtname=root.findViewById(R.id.txtfirstname)
        txtphone=root.findViewById(R.id.txtphone)
        txtemail=root.findViewById(R.id.txtEmail)
        txtLineID=root.findViewById(R.id.txtline2)
        imgpro=root.findViewById(R.id.imgpro)
        btnrequest=root.findViewById(R.id.imageButtonWait)
        btnpay=root.findViewById(R.id.imageButtonPass)
        btneditbank=root.findViewById(R.id.btneditbank)
        btneditprofile=root.findViewById(R.id.btnedituser3)
        txtcoutWaitpay=root.findViewById(R.id.txtcountwaitpay)
       // btnWaitPay=root.findViewById(R.id.imageButtonWaitPay)

        btneditprofile?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerEditProfileFragment())
            fragmentTransaction.commit()
        }

        btneditbank?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerBankFragment())
            fragmentTransaction.commit()
        }
        btnrequest?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerMenuReauestFragment())
            fragmentTransaction.commit()
        }
        btnpay?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerMenuWaitingPayFragment())
            fragmentTransaction.commit()
        }

        btnlogout?.setOnClickListener {

            val sharePrefer = requireContext().getSharedPreferences(
                    LoginLoanerActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences

            //return to login page
            val intent = Intent(context, LoginLoanerActivity::class.java)
            startActivity(intent)
        }
        viewloaner()
        counts()
        return root
    }
    private fun counts(){

        var url: String = getString(R.string.root_url) + getString(R.string.countmenuBorrower_url) + loanerID

        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        ///////////////////////////
                        if(data.getString("count_request_loaner")=="0"){
                            txtcoutRequest?.isVisible=false
                        }else{
                            txtcoutRequest?.text=data.getString("count_request_loaner")
                        }
                        ///////////////////////////
                        if(data.getString("count_pay_loaner")=="0"){
                            txtcoutpay?.isVisible=false
                        }else{
                            txtcoutpay?.text=data.getString("count_pay_loaner")
                        }
                        ///////////////////////////
                        if(data.getString("count_Waitpay_loaner")=="0"){
                            txtcoutWaitpay?.isVisible=false
                        }else{
                            txtcoutWaitpay?.text=data.getString("count_Waitpay_loaner")
                        }


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
                txtcoutRequest?.isVisible=false
                txtcoutpay?.isVisible=false
                txtcoutWaitpay?.isVisible=false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun viewloaner(){

        var url: String = getString(R.string.root_url) + getString(R.string.Loaner_url) + loanerID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        ///////////////////////////

                        var fristname =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="$fristname $lastname"
                        txtphone?.text=data.getString("phone")
                        txtemail?.text=data.getString("email")
                        txtLineID?.text="LineID: "+data.getString("LineID")
                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBLoaner_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

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