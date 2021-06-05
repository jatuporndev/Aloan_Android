package com.example.aloan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


class BorrowerAccountFragment : Fragment() {
    var borrowerID:String?=null
    var btnlogout : Button? = null
    var btnpined:Button?=null

    var Menu1Waiting:ImageButton?=null
    var MenuUnpass:ImageButton?=null
    var MenuPass:ImageButton?=null


    var txtcountWaiting:TextView?=null
    var txtcountconfirm:TextView?=null
    var txtcountunpass:TextView?=null
    var btnedituser:Button?=null
    var btneditbank:Button?=null

    var txtname:TextView?=null
    var txtphone:TextView?=null
    var txtemail:TextView?=null
    var txtLineID:TextView?=null
    var imgpro:ImageView?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_borrower_account, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)

        btnlogout =root.findViewById(R.id.btnlogout)
        Menu1Waiting =root.findViewById(R.id.imageButtonWait)
        MenuUnpass=root.findViewById(R.id.imageButtonUnpass)
        MenuPass=root.findViewById(R.id.imageButtonPass)
        btnpined=root.findViewById(R.id.btnpined)
        txtcountWaiting=root.findViewById(R.id.txtcountwaiting)
        txtcountconfirm=root.findViewById(R.id.txtcountconfirm)
        txtname=root.findViewById(R.id.txtfirstname)
        txtemail=root.findViewById(R.id.txtEmail)
        txtphone=root.findViewById(R.id.txtphone)
        txtLineID=root.findViewById(R.id.txtline)
        imgpro =root.findViewById(R.id.imgpro)
        btnedituser=root.findViewById(R.id.btnedituser3)
        btneditbank=root.findViewById(R.id.btneditbank)
        txtcountunpass=root.findViewById(R.id.txtcountunpass)



        btneditbank?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerEditBankFragment())
            fragmentTransaction.commit()
        }
        btnedituser?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerEditProfileFragment())
            fragmentTransaction.commit()
        }
        imgpro?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerEditProfileFragment())
            fragmentTransaction.commit()
        }


        Menu1Waiting?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuWaitingFragment())
            fragmentTransaction.commit()
        }
        MenuUnpass?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuUnpassFragment())
            fragmentTransaction.commit()
        }
        MenuPass?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuConfirmedFragment())
            fragmentTransaction.commit()
        }





        btnpined?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerPinedFragment())
            fragmentTransaction.commit()
        }

        btnlogout?.setOnClickListener {
            val sharePrefer = requireContext().getSharedPreferences(
                    LoginBorrowerActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences

            //return to login page
            val intent = Intent(context, LoginBorrowerActivity::class.java)
            startActivity(intent)
        }
        counts()
        viewborrower()

        return root
    }
    private fun counts(){

        var url: String = getString(R.string.root_url) + getString(R.string.countmenuBorrower_url) + borrowerID

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
                        if(data.getString("count_waiting")=="0"){
                            txtcountWaiting?.isVisible=false
                        }else{
                            txtcountWaiting?.text=data.getString("count_waiting")
                        }
                        ///////////////////////////
                        if(data.getString("count_confirm")=="0"){
                            txtcountconfirm?.isVisible=false
                        }else{
                            txtcountconfirm?.text=data.getString("count_confirm")
                        }
                        if(data.getString("count_unpass")=="0"){
                            txtcountunpass?.isVisible=false
                        }else{
                            txtcountunpass?.text=data.getString("count_unpass")
                        }


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
                txtcountWaiting?.isVisible=false
                txtcountconfirm?.isVisible=false
                txtcountunpass?.isVisible=false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun viewborrower(){

        var url: String = getString(R.string.root_url) + getString(R.string.Borrower_url) + borrowerID
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
                                getString(R.string.profileBorrower_image_url) + data.getString("imageProfile")
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