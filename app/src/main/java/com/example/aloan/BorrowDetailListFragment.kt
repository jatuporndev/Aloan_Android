package com.example.aloan

import  android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BorrowDetailListFragment : Fragment() {
    var recyclerView: RecyclerView?=null
    var loanerID: String? = null
    var txtmoneyMax: TextView?=null
    var txtmoneyMin: TextView?=null
    var txtinterest: TextView?=null
    var txtinterest_penalty: TextView?=null
    var txtinstullment_max:TextView?=null

    var txtname: TextView?=null
    var txtemail: TextView?=null
    var txtphone: TextView?=null
    var txtlineId: TextView?=null
    var imgpro:ImageView?=null

    var pin :ImageView?=null
    var back:ImageView?=null
    var editRequest:EditText?=null
    var btnaddnum:Button?=null
    var btnminusnum:Button?=null
    var btnreaquest:Button?=null
    var txtinstullment:TextView?=null
    var txtno:TextView?=null
    var borrowerID:String?=null
    var progressBar:ProgressBar?=null

    var money_max:String?=""
    var money_min:String?=""
    var Interest:String?=""
    var Interest_penalty:String?=""
    var backlist:String?=null
    var borrowelistID:String?=null

    var borrowerCriMoneyMax:String?=null
    var criterionID:String?=null

    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_borrow_detail_list, container, false)
        val bundle = this.arguments

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)
//test2
        recyclerView=root.findViewById(R.id.recyclerView)
        txtmoneyMax=root.findViewById(R.id.txtmoneyMax)
        txtmoneyMin=root.findViewById(R.id.txtmoneyRequestB)
        txtinterest=root.findViewById(R.id.txtiInterest_requestB)
        txtinterest_penalty=root.findViewById(R.id.txtInterest_penalty_requestB)
        txtlineId=root.findViewById(R.id.txtlineB)
        txtname=root.findViewById(R.id.txtnameB)
        txtemail=root.findViewById(R.id.txtemailB)
        txtphone=root.findViewById(R.id.txtPhoneB)
        pin=root.findViewById(R.id.imageButtonpin)
        back=root.findViewById(R.id.imageViewback)
        btnaddnum=root.findViewById(R.id.btnaddone)
        btnminusnum=root.findViewById(R.id.btnminus)
        btnreaquest=root.findViewById(R.id.btnreq)
        txtinstullment=root.findViewById(R.id.txtinstullment)
        editRequest=root.findViewById(R.id.editrequMoney)
        imgpro=root.findViewById(R.id.imgpro)
        txtno=root.findViewById(R.id.txtno)
        progressBar=root.findViewById(R.id.progressBar)
        txtinstullment_max=root.findViewById(R.id.txtinstullmentB)

        backlist=bundle?.get("backlist").toString()
        borrowelistID=bundle?.get("borrowlistID").toString()

        txtinstullment?.text="1"
        btnminusnum?.setOnClickListener {
            var lQuantity =
                    Integer.valueOf(txtinstullment?.text.toString())
            lQuantity -= 1
            if (lQuantity == 0) lQuantity = 1
            txtinstullment?.setText(lQuantity.toString())
        }

        btnaddnum?.setOnClickListener {
            var lQuantity =
                    Integer.valueOf(txtinstullment?.text.toString())
            lQuantity += 1
            txtinstullment?.setText(lQuantity.toString())
        }

        btnreaquest?.setOnClickListener {
            var money_max:Float? = money_max?.toFloat()
            var money_min:Float? = money_min?.toFloat()
            var moneyRequest:Float?=editRequest?.text.toString().toFloat()
            var crimax:Float? = borrowerCriMoneyMax?.toFloat()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("แจ้งเตือน")
            builder.setMessage("ยืนยันการส่งคำขอกู้")
                    .setCancelable(false)
                    .setPositiveButton("ยืนยัน") { dialog, id ->
                        // ใช่
                        if(moneyRequest!! < money_min!! || moneyRequest!! > money_max!!){
                            Toast.makeText(requireContext(), "จำนวนเงินไม่ตรงเงื่อนไข", Toast.LENGTH_LONG).show()
                        }else{
                            if(moneyRequest <= crimax!!){

                                if(viewrequast(borrowerID,borrowelistID)){
                                    addrequest(borrowerID,borrowelistID)
                                    Toast.makeText(requireContext(), "ส่งคำขอสำเร็จแล้ว", Toast.LENGTH_LONG).show()
                                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                    fragmentTransaction.addToBackStack(null)
                                    fragmentTransaction.replace(R.id.nav_host_fragment,BorrowerAccountFragment())
                                    fragmentTransaction.commit()
                                }else{
                                    Toast.makeText(requireContext(), "มีคำขออยู่แล้ว", Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toast.makeText(requireContext(), "ข้อมูลของคุณไม่ตรงกับเกณฑ์ที่ผู้ให้กู้กำหนดไว้", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                    .setNegativeButton("ยกเลิก") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()


        }
        viewBorrow(bundle?.get("LoanerID").toString())
        Handler().postDelayed({

            viewborrower()
            showCriterion(borrowelistID!!)
        }, 0)

        return root
    }
    @SuppressLint("SetTextI18n")
    private fun viewBorrow(loanerID: String?) {

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_showborrowlist_url) + loanerID
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

                        txtmoneyMax?.text = data.getString("money_max")+"฿"
                        money_max=data.getString("money_max")
                        txtmoneyMin?.text = data.getString("money_min")+"฿"
                        money_min =data.getString("money_min")
                        txtinterest?.text = data.getString("interest")+"%"
                        Interest=data.getString("interest")
                        txtinterest_penalty?.text = data.getString("Interest_penalty")+"%"
                        Interest_penalty=data.getString("Interest_penalty")
                        var name =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="คุณ $name $lastname"
                        txtemail?.text=data.getString("email")
                        txtphone?.text=data.getString("phone")
                        txtlineId?.text=data.getString("LineID")
                        txtinstullment_max?.text=data.getString("instullment_max")

                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBLoaner_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

                        back?.setOnClickListener {
                                var fm :Fragment=BorrowerPinedFragment()
                            if (backlist=="pin"){
                                 fm = BorrowerPinedFragment()
                            }
                            if (backlist=="waiting"){
                                 fm = BorrowerMenuWaitingFragment()
                            }
                            if (backlist=="home"){
                                 fm = BorrowerHomeFragment()
                            }
                            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.replace(R.id.nav_host_fragment,fm)
                            fragmentTransaction.commit()
                        }
                        if(viewpined(borrowerID,data.getString("borrowlistID"))){
                            pin?.setBackgroundResource(R.drawable.ic_baseline_push_pin_24)
                        }else{
                            pin?.setBackgroundResource(R.drawable.ic_baseline_push_pin_24_non)
                        }

                        pin?.setOnClickListener {
                            if(viewpined(borrowerID,data.getString("borrowlistID"))){
                                deletepined(borrowerID,data.getString("borrowlistID"))
                                viewBorrow(data.getString("LoanerID"))
                                Toast.makeText(requireContext(), "ลบออกจากรายการโปรด", Toast.LENGTH_LONG).show()
                            }else{
                                addpined(borrowerID,data.getString("borrowlistID"))
                                viewBorrow(data.getString("LoanerID"))
                                Toast.makeText(requireContext(), "เพิ่มลงรายการโปรด", Toast.LENGTH_LONG).show()
                            }


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
    private fun showCriterion(borrowlistID: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_showCriterion_url)+borrowlistID+"?criterionID="+criterionID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                    item.getString("criterionID"),
                                    item.getString("Age_range"),
                                    item.getString("Saraly_range"),
                                    item.getString("Married"),
                                    item.getString("money_max"),
                                    item.getString("borrowlistID"),
                                    item.getString("instullment_max"),
                                    item.getString("edit")

                            )
                            )
                            txtno?.visibility=View.GONE
                            progressBar?.visibility=View.GONE
                            recyclerView!!.adapter = DataAdapter(data)

                        }
                    } else {
                        recyclerView!!.adapter = DataAdapter(data)
                        progressBar?.visibility=View.GONE
                        txtno?.text="ไม่มีเกณฑ์"


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

    internal class Data(
            var criterionID: String,var Age_range: String,var Saraly_range: String,var Married: String,
            var money_max: String,var borrowlistID: String,var instullment_max: String,var edit:String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_criterion,
                    parent, false

            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var arrayAge = arrayOf("18-28 ปี","29-39 ปี","40-50 ปี","51ปีขึ้นไป")
            var arrayMarri = arrayOf("โสด","แต่งงานแล้ว")
            var arraySalary = arrayOf("0-9000","9000-15000","15000-50000","มากกว่า5หมื่น")

            val data = list[position]
            holder.data = data
            if (criterionID==data.criterionID){
            holder.txtageR.text=arrayAge[data.Age_range.toInt()]
            holder.txtmarriR.text=arrayMarri[data.Married.toInt()]
            holder.txtsalaryR.text=arraySalary[data.Saraly_range.toInt()]
            holder.txtmoneyR.text=data.money_max+"฿"
            holder.brndelete.isVisible=false
            holder.txtmoneyR.setTextColor(Color.parseColor("#FF000000"))
            holder.txtinstullment_max.text=data.instullment_max
            holder.txtnew.visibility=View.GONE



                holder.txtsalaryR.setTextColor(Color.parseColor("#33BC40"))
                holder.txtmarriR.setTextColor(Color.parseColor("#33BC40"))
                holder.txtageR.setTextColor(Color.parseColor("#33BC40"))
                holder.txtmoneyR.setTextColor(Color.parseColor("#FF612F"))
            }


        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtageR: TextView = itemView.findViewById(R.id.txtageR)
            var txtsalaryR: TextView = itemView.findViewById(R.id.txtsalaryR)
            var txtmarriR: TextView = itemView.findViewById(R.id.txtmarriR)
            var txtmoneyR: TextView = itemView.findViewById(R.id.txtmoneyR)
            var brndelete:ImageButton= itemView.findViewById(R.id.imageButtondelete)
            var txtinstullment_max:TextView=itemView.findViewById(R.id.txtinstu)
            var txtnew:TextView=itemView.findViewById(R.id.txtnew)


        }
    }
    private fun viewpined(borrowerID: String? ,BorrowelistID: String?):Boolean {

        var check :Boolean=false
        var url: String = getString(R.string.root_url) + getString(R.string.pined_url) + borrowerID+","+BorrowelistID
        Log.d("rrr4",url)
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
                        check=true
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
        Log.d("rrr4", check.toString())
        return check
    }
    private fun addpined(borrowerID: String? ,BorrowelistID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.addpined_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("BorrowerID", borrowerID!!)
                .add("borrowlistID", BorrowelistID!!)

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
    private fun deletepined(borrowerID: String? ,BorrowelistID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.deletepined_url) + borrowerID+","+BorrowelistID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .delete()
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
    private fun addrequest(borrowerID: String? ,BorrowelistID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.addrequest_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("Money", editRequest?.text.toString())
                .add("instullment", txtinstullment?.text.toString())
                .add("Interest", Interest.toString())
                .add("Interest_penalty", Interest_penalty.toString())
                .add("BorrowerID", borrowerID!!)
                .add("borrowlistID", BorrowelistID!!)
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
    private fun viewrequast(borrowerID: String? ,BorrowelistID: String?):Boolean {

        var check :Boolean=true
        var url: String = getString(R.string.root_url) + getString(R.string.viewrequest_url) +"?BorrowerID="+ borrowerID+"&borrowlistID="+BorrowelistID
        Log.d("www2",url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONArray(response.body!!.string())
                    if (data.length() > 0) {
                        check=false
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
        return check
    }
    private fun viewborrower(){

        var url: String = getString(R.string.root_url) + getString(R.string.Borrower_url) + borrowerID
        Log.d("text2",url)
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

                        var  borrowerAge = getAge(data.getString("birthday"))
                        var borrowerSalary = data.getString("salary").toDouble()
                        var borrowerMarri =data.getString("married")
                        var age_range:String?=null
                        var saraly_range:String?=null
                        Log.d("text2",borrowerAge.toString())
                        when(borrowerAge) {
                            in 18..28 ->  age_range="0"
                            in 29..39 -> age_range="1"
                            in 40..50 -> age_range="2"
                            in 51..Int.MAX_VALUE-> age_range="3"
                            else -> age_range="000"
                        }
                        when(borrowerSalary) {
                            in 0..9000 ->  saraly_range="0"
                            in 9001..15000 -> saraly_range="1"
                            in 15001..50000 -> saraly_range="2"
                            in 50001..Int.MAX_VALUE -> saraly_range="3"
                            else -> println("no")
                        }

                        viewCriterion(
                                age_range,
                                saraly_range,
                                borrowerMarri
                        )

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
    private fun viewCriterion(age_range: String?,saraly_range: String?,borrowerMarri: String?){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_checkCriterion_url) +"?borrowlistID="+borrowelistID+
                "&Age_range="+age_range+"&Saraly_range="+saraly_range+"&Married="+borrowerMarri
        Log.d("text2",url)
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
                        borrowerCriMoneyMax=data.getString("money_max")
                        criterionID=data.getString("criterionID")

                    }else{
                        borrowerCriMoneyMax=Int.MAX_VALUE.toString()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    borrowerCriMoneyMax=Int.MAX_VALUE.toString()
                }
            } else {
                response.code
                //borrowerCriMoneyMax=Int.MAX_VALUE.toString()
                borrowerCriMoneyMax="0"
            }
            Log.d("text2",borrowerCriMoneyMax.toString())

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
