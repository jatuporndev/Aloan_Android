package com.example.aloan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerEditBankFragment : Fragment() {

    var editbank:Spinner?=null
    var editbanknumber:EditText?=null
    var back:ImageView?=null
    var btnconfirm:Button?=null
    var imgpro:ImageView?=null

    var borrowerID:String?=null
    var banklistID=""
    var bankname=""
    var bankimg=""
    private var bank = java.util.ArrayList<Bank>()
    private var bankPosition = java.util.ArrayList<BankPosition>()

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
        imgpro=root.findViewById(R.id.imgpro)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerAccountFragment())
            fragmentTransaction.commit()
        }
        btnconfirm?.setOnClickListener {
            updateUser()
        }
        listbank()

        val adapterband = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, bank)
        editbank?.adapter = adapterband
        viewborrower()

        editbank?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = editbank!!.selectedItem as Bank
                banklistID = band.banklistID
                bankname=band.bankname
                bankimg=band.imagebank
                var url = getString(R.string.root_url) +
                        getString(R.string.bank_image_url) + bankimg
                Picasso.get().load(url).into(imgpro)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        Log.d("test1",  bankPosition.binarySearch(BankPosition("ธนาคารกรุงเทพ"),compareBy<BankPosition> { it.bankname}.thenBy { it.bankname}).toString())




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
                      //  editbank?.setText(data.getString("bank"))
                        val bank = data.getString("bank")
                        editbank?.setSelection( bankPosition.binarySearch(BankPosition(bank),compareBy<BankPosition> { it.bankname}.thenBy { it.bankname}))



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
                .addFormDataPart("Bank", bankname.toString())

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
    class Bank(var banklistID: String, var bankname: String,var imagebank: String) {
        override fun toString(): String {
            return bankname
        }

    }
    class BankPosition(var bankname: String) {
        override fun toString(): String {
            return bankname
        }

    }
    private fun listbank() {

        val urlProvince: String = getString(R.string.root_url) + getString(R.string.loaner_allbank_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            bank.add(
                                    Bank(
                                            item.getString("banklistID"),
                                            item.getString("bankname"),
                                            item.getString("imagebank")
                                    )
                            )

                            bankPosition.add(
                                    BankPosition(
                                            item.getString("bankname")
                                    )
                            )


                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }
}