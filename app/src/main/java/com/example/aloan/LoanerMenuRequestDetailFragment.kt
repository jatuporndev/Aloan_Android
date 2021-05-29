package com.example.aloan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class LoanerMenuRequestDetailFragment : Fragment() {

    var txtname:TextView?=null
    var txtemail:TextView?=null
    var txtphone:TextView?=null
    var txtLineId:TextView?=null
    var txtBirthday:TextView?=null
    var txtage:TextView?=null
    var txtaddress:TextView?=null
    var txtgender:TextView?=null
    var imggender:ImageView?=null

    var txtmoneyRequest:TextView?=null
    var txtinstullmentRequest:TextView?=null
    var txtInterestRequest:TextView?=null
    var txtInterest_penaltyRequest:TextView?=null
    var date:TextView?=null
    var back:ImageView?=null
    var imgpro:ImageView?=null

    var editmoneyRequest:EditText?=null
    var editinstullmentRequest:EditText?=null
    var btnpass:Button?=null
    var btnunpass:Button?=null
    var btnhistory:Button?=null
    var checkbox:CheckBox?=null

    var comment:String?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_menu_request_detail, container, false)
        val bundle = this.arguments


        txtname =root.findViewById(R.id.txtnameB)
        txtemail=root.findViewById(R.id.txtemailB)
        txtphone=root.findViewById(R.id.txtPhoneB)
        txtLineId=root.findViewById(R.id.txtlineB)
        txtBirthday=root.findViewById(R.id.txtbirsthdayB)
        txtage=root.findViewById(R.id.txtageB)
        txtaddress=root.findViewById(R.id.txtaddressB)
        txtgender=root.findViewById(R.id.txtgenderB)
        imggender=root.findViewById(R.id.imggenderB)

        txtmoneyRequest=root.findViewById(R.id.txtmoneyRequestB)
        txtinstullmentRequest=root.findViewById(R.id.txtinstullmentB)
        txtInterestRequest=root.findViewById(R.id.txtiInterest_requestB)
        txtInterest_penaltyRequest=root.findViewById(R.id.txtInterest_penalty_requestB)
        date=root.findViewById(R.id.txtdayReB)
        back=root.findViewById(R.id.imageViewback)
        imgpro=root.findViewById(R.id.imgpro)

        editmoneyRequest=root.findViewById(R.id.editmoneyB)
        editinstullmentRequest=root.findViewById(R.id.editinstullmentB)
        btnpass=root.findViewById(R.id.btnpass)
        btnunpass=root.findViewById(R.id.btnunpass)
        btnhistory=root.findViewById(R.id.btnhstoryB)
        checkbox=root.findViewById(R.id.checkBox2)

        btnpass?.isEnabled=false
        btnunpass?.isEnabled=false

        checkbox?.setOnClickListener {
            btnpass?.isEnabled = checkbox!!.isChecked
            btnunpass?.isEnabled = checkbox!!.isChecked
        }

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerMenuReauestFragment())
            fragmentTransaction.commit()
        }
        btnunpass?.setOnClickListener {
            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("ระบุเเหตุผล")

            val input = EditText(requireContext())
            input.hint = "ไม่ผ่านเพราะ..."
            input.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            input.isSingleLine = false //add this
            input.setLines(1)
            input.maxLines = 5
            input.gravity = Gravity.LEFT or Gravity.TOP
            input.isHorizontalScrollBarEnabled = false //this

            builder.setView(input)

            builder.setPositiveButton("ตกลง", DialogInterface.OnClickListener { dialog, which ->
                ////////////////////////////////////////////////////
                comment= input.text.toString()
                updateUser(bundle?.get("RequestID").toString())
            })
            builder.setNegativeButton("ยกเลิก", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }


        viewdetailborroweer(bundle?.get("RequestID").toString())

        return root
    }

    private fun viewdetailborroweer(RequrstID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_DetailMenu1request_url) + RequrstID
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
                        txtLineId?.text=data.getString("LineID")
                        txtBirthday?.text=data.getString("birthday")
                        txtage?.text=getAge(data.getString("birthday")).toString()
                        txtaddress?.text=data.getString("address")

                        txtmoneyRequest?.text=data.getString("Money")
                        txtinstullmentRequest?.text=data.getString("instullment_request")
                        editmoneyRequest?.setText(data.getString("Money"))
                        editinstullmentRequest?.setText(data.getString("instullment_request"))
                        txtInterestRequest?.text=data.getString("Interest_request")
                        txtInterest_penaltyRequest?.text=data.getString("Interest_penalty_request")
                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBorrower_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

                        if (data.getString("gender")=="0"){
                            txtgender?.text="ชาย"
                            imggender?.setImageResource(R.drawable.male)
                        }else{
                            txtgender?.text="หญิง"
                            imggender?.setImageResource(R.drawable.femenine)
                        }

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

    private fun updateUser(RequrstID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_updateUnpass_url) + RequrstID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("comment", comment!!)
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
                        Toast.makeText(context, "ยกเลิกแล้ว", Toast.LENGTH_LONG).show()

                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment, LoanerMenuReauestFragment())
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


    @SuppressLint("SimpleDateFormat")
    private fun getAge(dobString: String): Int {
        var date: Date? = null
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) return 0
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.time = date
        val year: Int = dob.get(Calendar.YEAR)
        val month: Int = dob.get(Calendar.MONTH)
        val day: Int = dob.get(Calendar.DAY_OF_MONTH)
        dob.set(year, month + 1, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}