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
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerEditBankFragment : Fragment() {

    var editbank: TextView?=null
    var editbanknumber: TextView?=null
    var back: ImageView?=null
    var btnconfirm: Button?=null
    var editholdername:TextView?=null

    var loanerID:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_edit_bank, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)

        editbank=root.findViewById(R.id.editBank)
        editbanknumber=root.findViewById(R.id.editbanknumber)
        back=root.findViewById(R.id.imageviewback)
        btnconfirm=root.findViewById(R.id.btnconfrim1)
        editholdername=root.findViewById(R.id.editBank2)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        btnconfirm?.setOnClickListener {
            addBank()
        }

        return root
    }

    private fun addBank()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_addBank_url) + loanerID
        val okHttpClient = OkHttpClient()
        var request: Request
        val formBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("bankNumber", editbanknumber?.text.toString())
            .addFormDataPart("bank", editbank?.text.toString())
            .addFormDataPart("holderName", editholdername?.text.toString())

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
                        fragmentTransaction.replace(R.id.nav_host_fragment,LoanerBankFragment())
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