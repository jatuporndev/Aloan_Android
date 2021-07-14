package com.example.aloan

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerMenuConfirmDetailFragment : Fragment() {

    var back:ImageView?=null
    var txtdateRequest:TextView?=null
    var txtdateconfirm:TextView?=null
    var txtloanername:TextView?=null
    var txtloaneremail:TextView?=null
    var txtloanerphone:TextView?=null
    var txtloanerline:TextView?=null
    var borrowerID:String?=null
    var txtmoneyRequest:TextView?=null
    var txtinstullmentRequest:TextView?=null
    var txtmoneyConfirm:TextView?=null
    var txtinstullmentConfirm:TextView?=null
    var checkbox:CheckBox?=null
    var imgpro:ImageView?=null
    var btnok:Button?=null
    var btncancle:Button?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_menu_confirm_detail, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)
        val bundle = this.arguments
        txtdateRequest=root.findViewById(R.id.txtdayReB)
        txtdateconfirm=root.findViewById(R.id.txtdayReB2)
        txtloanername=root.findViewById(R.id.txtnameB)
        txtloaneremail=root.findViewById(R.id.txtemailB)
        txtloanerphone=root.findViewById(R.id.txtPhoneB)
        txtloanerline=root.findViewById(R.id.txtlineB)
        imgpro=root.findViewById(R.id.imgpro)
        txtmoneyRequest=root.findViewById(R.id.txtmoney_amount)
        txtinstullmentRequest=root.findViewById(R.id.txtinstullmentB)
        txtmoneyConfirm=root.findViewById(R.id.txtmoneyconfirmB)
        txtinstullmentConfirm=root.findViewById(R.id.txtinstullmentconfirmB)
        checkbox=root.findViewById(R.id.checkBox2)
        back=root.findViewById(R.id.imageViewback)
        btnok=root.findViewById(R.id.btnpass)
        btncancle=root.findViewById(R.id.btnunpass)

        btnok?.isEnabled=false
        btncancle?.isEnabled=false

        checkbox?.setOnClickListener {
            btnok?.isEnabled = checkbox!!.isChecked
            btncancle?.isEnabled = checkbox!!.isChecked
        }

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuConfirmedFragment())
            fragmentTransaction.commit()
        }

        btncancle?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("แจ้งเตือน")
            builder.setMessage("ยืนยันการยกเลิกหรือไม่่")
                .setCancelable(false)
                .setPositiveButton("ยืนยัน") { dialog, id ->
                    // ใช่
                    Cancel(bundle?.get("RequestID").toString())
                }
                .setNegativeButton("ยกเลิก") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        btnok?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("แจ้งเตือน")
            builder.setMessage("ยืนยันการที่หรือไม่?\n*เมื่อกดยอมรับแล้ว รายการอื่นจะถูกยกเลิกทันที โปรดตรวจสอบให้แน่ใจว่าที่คุณเลือกถูกต้อง")
                    .setCancelable(false)
                    .setPositiveButton("ยืนยัน") { dialog, id ->
                        // ใช่
                        updateAccept(bundle?.get("RequestID").toString())
                    }
                    .setNegativeButton("ยกเลิก") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()
        }

        viewConfirmdetail(bundle?.get("RequestID").toString())
        return root
    }
    private fun viewConfirmdetail(RequrstID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.viewConfirmedDetail_url) + RequrstID
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
                        txtloanername?.text="$fristname $lastname"
                        txtdateRequest?.text=data.getString("dateRe")
                        txtdateconfirm?.text=data.getString("dateCheck")
                        txtloaneremail?.text=data.getString("email")
                        txtloanerphone?.text=data.getString("phone")
                        txtloanerline?.text=data.getString("LineID")

                        txtmoneyRequest?.text="฿"+data.getString("Money")
                        txtinstullmentRequest?.text=data.getString("instullment_request")
                        txtmoneyConfirm?.text="฿"+data.getString("money_confirm")
                        txtinstullmentConfirm?.text=data.getString("instullment_confirm")

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
    private fun updateAccept(RequrstID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updateAccept_url) + RequrstID
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
                    updatecancleRequest()
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuWaitingFragment())
                    fragmentTransaction.commit()
                    Toast.makeText(requireContext(), "คุณได้กดยอมรับแล้ว รอผู้ให้กู้โอนเงินซักครู่", Toast.LENGTH_LONG).show()

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
    private fun updatecancleRequest()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updatecancleRequest_url) + borrowerID
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

    private fun Cancel(RequrstID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_updateUnpass_url) + RequrstID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
            .add("comment", "ยกเลิก")
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuUnpassFragment())
                    fragmentTransaction.commit()
                    Toast.makeText(requireContext(), "\n ยกเลิกสำเร็จ", Toast.LENGTH_LONG).show()

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