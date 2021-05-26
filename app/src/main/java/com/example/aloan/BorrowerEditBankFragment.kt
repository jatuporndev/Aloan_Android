package com.example.aloan

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerEditBankFragment : Fragment() {

    var editbank:TextView?=null
    var editbanknumber:TextView?=null
    var back:ImageView?=null
    var btnconfirm:Button?=null

    var borrowerID:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_edit_bank, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)

        editbank=root.findViewById(R.id.editBank)
        editbanknumber=root.findViewById(R.id.editbanknumber)
        back=root.findViewById(R.id.imageviewback)
        btnconfirm=root.findViewById(R.id.btnconfrim1)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerAccountFragment())
            fragmentTransaction.commit()
        }
        btnconfirm?.setOnClickListener {
            updateUser()
        }
        viewborrower()
        return root
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
                        editbanknumber?.setText(data.getString("IDBank"))
                        editbank?.setText(data.getString("bank"))


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
    private fun updateUser()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.BorrowerUpdate_url) + borrowerID
        val okHttpClient = OkHttpClient()
        var request: Request
            val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("typeUpdate", "bank")
                .addFormDataPart("IDBank", editbanknumber?.text.toString())
                .addFormDataPart("Bank", editbank?.text.toString())

                .build()
            request= Request.Builder()
                .url(url)
                .post(formBody)
                .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(context, "สำเร็จ", Toast.LENGTH_LONG).show()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment,BorrowerAccountFragment())
                        fragmentTransaction.commit()

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