package com.example.aloan

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerBorrowlistEditFragment : Fragment() {

    var loanerID: String? = null
    var editmoneyMax:EditText?=null
    var editmoneyMin:EditText?=null
    var editinterest:EditText?=null
    var editInterest_penalty:EditText?=null
    var btnconedit:Button?=null
    var btncancel:Button?=null
    var txtname:TextView?=null
    var editinstu:EditText?=null




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root= inflater.inflate(R.layout.fragment_loaner_borrowlist_edit, container, false)
        val bundle =this.arguments
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        editmoneyMax=root.findViewById(R.id.editmoneyMax)
        editmoneyMin=root.findViewById(R.id.editmoneyMin)
        editinterest=root.findViewById(R.id.editinterest)
        editInterest_penalty=root.findViewById(R.id.editInterest_penalty)
        btnconedit=root.findViewById(R.id.btnconedit)
        btncancel=root.findViewById(R.id.btncancel)
        txtname=root.findViewById(R.id.txtname)
        editinstu=root.findViewById(R.id.editintu)
        Log.d(("testt"),bundle?.get("interestMax").toString())
        btnconedit?.setOnClickListener {
            if(editinterest?.text.toString().toFloat()>15 || editInterest_penalty?.text.toString().toFloat()>(bundle?.get("interestMax").toString().toFloat()+3) ){
                Toast.makeText(requireContext(), "ระบุดอกเบี้ยไม่เกินกฎหมายกำหนด", Toast.LENGTH_LONG).show()
            }else{
                update()
            }

        }

        btncancel?.setOnClickListener {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,LoanerHomeFragment())
            fragmentTransaction.commit()
        }
        viewlist(loanerID)
        return root
    }
    private fun viewlist(userID: String?) {

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_showborrowlist_url) + userID
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

                        editmoneyMax?.setText(data.getString("money_max"))
                        editmoneyMin?.setText(data.getString("money_min"))
                        editinterest?.setText(data.getString("interest"))
                        editinstu?.setText(data.getString("instullment_max"))
                        editInterest_penalty?.setText(data.getString("Interest_penalty"))
                        var name =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="สวัสดี $name $lastname"

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
    private fun update()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_updateborrowlist_url) + loanerID
        val okHttpClient = OkHttpClient()
        var request: Request

        val formBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("money_min", editmoneyMin?.text.toString())
            .addFormDataPart("money_max", editmoneyMax?.text.toString())
            .addFormDataPart("interest", editinterest?.text.toString())
            .addFormDataPart("Interest_penalty", editInterest_penalty?.text.toString())
                .addFormDataPart("instullment_max", editinstu?.text.toString())
            .build()
        request= Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment,LoanerHomeFragment())
                    fragmentTransaction.commit()

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
