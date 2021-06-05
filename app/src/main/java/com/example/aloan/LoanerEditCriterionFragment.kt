package com.example.aloan

import android.app.Dialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class LoanerEditCriterionFragment : Fragment() {

    var txtage:TextView?=null
    var txtsaraly:TextView?=null
    var txtmarri:TextView?=null
    var editinstullment_max:EditText?=null
    var editmoney_max:EditText?=null
    var btncancel:Button?=null
    var btnback:ImageView?=null
    var btnupdate:Button?=null
    var loanerID: String? = null
    var txtraw:TextView?=null
    var sc=""
    var index=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loaner_edit_criterion, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        val bundle = this.arguments

        sc =bundle?.get("sc").toString()
        index=bundle?.get("index").toString()

        txtage=root.findViewById(R.id.txtageRange)
        txtsaraly=root.findViewById(R.id.txtsaralyRange)
        txtmarri=root.findViewById(R.id.txtmarri)
        editmoney_max=root.findViewById(R.id.editmoneymax)
        editinstullment_max=root.findViewById(R.id.editinstu)
        btncancel=root.findViewById(R.id.btncancel)
        btnupdate=root.findViewById(R.id.btnconedit)
        btnback=root.findViewById(R.id.btnback)
        txtraw=root.findViewById(R.id.txtraw)

        btnback?.setOnClickListener {


            val bundle = Bundle()
            bundle.putString("scroll",sc)
            bundle.putString("index",index)
            val fm = LoanerHomeFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        btnupdate?.setOnClickListener {
            if(editmoney_max?.text.toString().toFloat() > bundle?.get("moneyMax").toString().toFloat() ||
                    editinstullment_max?.text.toString().toFloat()>bundle?.get("instullmentMax").toString().toFloat()){
                Toast.makeText(context, "จำนวนงวดสูงสุดหรือจำนวนเงินสุด ต้องไม่เกินค่าเริ่มต้น", Toast.LENGTH_LONG).show()

            }else{
                update(bundle?.get("criterionID").toString())
            }

        }

        var view:View = layoutInflater.inflate(R.layout.read,null)
        var btnreaded:Button = view.findViewById(R.id.btnreaded)

        var txtpp:TextView=view.findViewById(R.id.txtpp)

        var text:String=""
        try {
            var io: InputStream = requireContext().assets.open("raw.txt")
            var size:Int = io.available()
            var buffer=ByteArray(size)
            io.read(buffer)
            io.close()
            text= String(buffer)
        }catch (ex : IOException){
            ex.printStackTrace()
        }
        txtpp.text=text

        var dialog: Dialog = Dialog(requireContext(),android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight)
        dialog.setContentView(view)
        txtraw?.setOnClickListener {
            dialog.show()
        }
        btnreaded?.setOnClickListener {
            dialog.dismiss()
        }
        txtraw?.paintFlags = txtraw?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!
        viewcriterion(bundle?.get("criterionID").toString())

        return root
    }
    private fun viewcriterion(criterionID: String?) {

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_viewcriterion_url) + criterionID
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
                    var arrayAge = arrayOf("18-28 ปี","29-39 ปี","40-50 ปี","51ปีขึ้นไป")
                    var arrayMarri = arrayOf("โสด","แต่งงานแล้ว")
                    var arraySalary = arrayOf("0-9000","9000-15000","15000-50000","มากกว่า5หมื่น")
                    if (data.length() > 0) {

                        txtage?.text=arrayAge[data.getInt("Age_range")]
                        txtsaraly?.text=arraySalary[data.getInt("Saraly_range")]
                        txtmarri?.text=arrayMarri[data.getInt("Married")]
                        editmoney_max?.setText(data.getString("money_max"))
                        editinstullment_max?.setText(data.getString("instullment_max"))


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
    private fun update(criterionID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_updatecriterion_url) + criterionID
        val okHttpClient = OkHttpClient()
        var request: Request

        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("money_max", editmoney_max?.text.toString())
                .addFormDataPart("instullment_max", editinstullment_max?.text.toString())
                .build()
        request= Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val bundle = Bundle()
                    bundle.putString("scroll",sc)
                    bundle.putString("index",index)
                    val fm = LoanerHomeFragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
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