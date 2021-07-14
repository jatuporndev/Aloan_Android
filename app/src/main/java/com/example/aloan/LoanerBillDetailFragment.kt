package com.example.aloan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class LoanerBillDetailFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var txtdayPay:TextView?=null
    var txtmoney:TextView?=null
    var txtmoneytotal:TextView?=null
    var imgslip:ImageView?=null
    var back:ImageView?=null
    var btnconfrim:Button?=null
    var btncancle:TextView?=null
    var txtmoneyfire:TextView?=null
    var checkBox:CheckBox?=null
    var borrowDetailID = ""
    var historyDetailID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_bill_detail, container, false)
        val bundle = this.arguments
        borrowDetailID = bundle?.get("BorrowDetailID").toString()
        historyDetailID = bundle?.get("historyDetailID").toString()
        recyclerView=root.findViewById(R.id.recyclerView4)
        txtdayPay=root.findViewById(R.id.txtdatepay)
        txtmoney=root.findViewById(R.id.txtmoney1)
        txtmoneytotal=root.findViewById(R.id.txtmoneytotal1)
        imgslip=root.findViewById(R.id.imgslip)
        back=root.findViewById(R.id.imageviewback)
        btnconfrim=root.findViewById(R.id.btnconfrimslip)
        btncancle=root.findViewById(R.id.btncancleslip)
        txtmoneyfire=root.findViewById(R.id.txtmoneyfire)
        checkBox=root.findViewById(R.id.checkBox7)

        btnconfrim?.isEnabled=false
        btncancle?.isEnabled=false

        checkBox?.setOnClickListener {
            btnconfrim?.isEnabled = checkBox?.isChecked!!
            btncancle?.isEnabled = checkBox?.isChecked!!
        }

        btncancle?.paintFlags = btncancle?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!

        viewdetail(bundle?.get("historyDetailID").toString())
        viewPaying(bundle?.get("historyDetailID").toString())

        back?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("BorrowDetailID", borrowDetailID)
            val fm = LoanerMenuGetMoneyDetailFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        btnconfrim?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("แจ้งเตือน")
            builder.setMessage("ตรวจสอบข้อมูลครบถ้วน")
                    .setCancelable(false)
                    .setPositiveButton("ใช่") { dialog, id ->
                        // ใช่
                        comfrim(historyDetailID)
                    }
                    .setNegativeButton("ยกเลิก") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()

        }
        btncancle?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("แจ้งเตือน")
            builder.setMessage("ยืนยันที่จะยกเลิกหรือไม่")
                .setCancelable(false)
                .setPositiveButton("ใช่") { dialog, id ->
                    // ใช่

                }
                .setNegativeButton("ยกเลิก") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        Log.d("testh",historyDetailID.toString()+" testt")

        return root
    }
    @SuppressLint("SetTextI18n")
    private fun viewdetail(historyDetailID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_billdetail_url) + historyDetailID
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

                        txtdayPay?.text=data.getString("datepaying")
                        txtmoney?.text="฿"+data.getString("money")
                        txtmoneytotal?.text="฿"+data.getString("money_total")
                        txtmoneyfire?.text="฿"+data.getString("fire")


                        var url = getString(R.string.root_url) +
                                getString(R.string.payment_image_url) + data.getString("imageBill")
                        Picasso.get().load(url).into(imgslip)

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
    private fun viewPaying(historyDetailID: String) {

        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_historylist_url)+historyDetailID
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
                                    item.getString("HistoryID"),
                                    item.getString("settlement_date"),
                                    item.getString("moneySet"),
                                    item.getString("dateset_status"),
                                    item.getString("interest_penalty_money")

                            )
                            )

                            recyclerView!!.adapter = DataAdapter(data)

                        }

                    } else {
                        recyclerView!!.adapter = DataAdapter(data)


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
            var HistoryID: String,var settlement_date: String,var moneySet:String,var dateset_status:String,
            var interest_penalty_money:String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_borrower_bill_pay,
                    parent, false

            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txtid.text="รหัสใบแจ้งหนี้ "+data.HistoryID
            holder.txtdate.text=data.settlement_date
            holder.txtmoney.text="฿"+data.moneySet
            holder.txtfire.visibility=View.INVISIBLE

            if (data.dateset_status=="1"){
                holder.txtfire.visibility=View.VISIBLE
                holder.txtfire.text="+฿"+data.interest_penalty_money
                holder.txterror.text="เลยวันกำหนด"
                holder.txtdate.setTextColor(Color.parseColor("#FF0000"))
                holder.checkBox.isChecked = true
                holder.checkBox.isEnabled = false
            }

            holder.checkBox.visibility=View.INVISIBLE


        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtdate: TextView = itemView.findViewById(R.id.txtdateset)
            var txtid: TextView = itemView.findViewById(R.id.txtid)
            var txtmoney: TextView = itemView.findViewById(R.id.txtmonetset)
            var txtfire: TextView = itemView.findViewById(R.id.txtfire)
            var checkBox: CheckBox =itemView.findViewById(R.id.checkBox4)
            var txterror:TextView=itemView.findViewById(R.id.textView101)



        }
    }
    private fun comfrim(historyDetailID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_confrimBill_url) + historyDetailID
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
                val bundle = Bundle()
                bundle.putString("BorrowDetailID", borrowDetailID)
                val fm = LoanerMenuGetMoneyDetailFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
                Toast.makeText(context, "ยืนยันสำเร็จแล้ว", Toast.LENGTH_LONG).show()
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

}