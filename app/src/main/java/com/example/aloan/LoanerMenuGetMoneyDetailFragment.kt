package com.example.aloan

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoanerMenuGetMoneyDetailFragment : Fragment() {
    var back: ImageView?=null
    var imgpro: ImageView?=null
    var txtborrowdetailID: TextView?=null
    var txtname: TextView?=null
    var txtdateStart: TextView?=null
    var txtphone: TextView?=null
    var txtemail: TextView?=null
    var txtline: TextView?=null
    var txtPrinciple: TextView?=null
    var txtinstullment_total: TextView?=null
    var txtInterest: TextView?=null
    var txtInterest_penalty: TextView?=null
    var txtmoney_amount: TextView?=null
    var txtinstullment_amont: TextView?=null
    var recyclerView: RecyclerView?=null
    var txttotalmoney: TextView?=null
    var btnpayment: Button?=null
    var txthis: TextView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loaner_menu_get_money_detail, container, false)
        val bundle = this.arguments

        recyclerView=root.findViewById(R.id.recyclerView2)
        back=root.findViewById(R.id.imageViewback)
        imgpro=root.findViewById(R.id.imgpro)
        txtborrowdetailID=root.findViewById(R.id.textborrowdetailID)
        txtdateStart=root.findViewById(R.id.txtdayReB)
        txtname=root.findViewById(R.id.txtnameB)
        txtphone=root.findViewById(R.id.txtPhoneB)
        txtemail=root.findViewById(R.id.txtemailB)
        txtline=root.findViewById(R.id.txtlineB)

        txtPrinciple=root.findViewById(R.id.txtPrinciple)
        txtinstullment_total=root.findViewById(R.id.txtinstullment_total)
        txtInterest=root.findViewById(R.id.txtInterest)
        txtinstullment_amont=root.findViewById(R.id.txtinstullment_amont)
        txtInterest_penalty=root.findViewById(R.id.txtInterest_penalty)
        txttotalmoney=root.findViewById(R.id.txttotalmoney)
        txtmoney_amount=root.findViewById(R.id.txtmoney_amount)
        btnpayment=root.findViewById(R.id.btnpass)
        txthis=root.findViewById(R.id.txthis)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerMenuGetMoneyFragment())
            fragmentTransaction.commit()
        }

        viewdetail(bundle?.get("BorrowDetailID").toString())
        showBill(bundle?.get("BorrowDetailID").toString())


        return root
    }
    @SuppressLint("SetTextI18n")
    private fun viewdetail(BorrowDetailID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_ManuGetMoneydetail_url) + BorrowDetailID
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
                        if (data.getString("instullment_Amount") =="0"){
                            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
                            fragmentTransaction.commit()
                        }
                        var fristname =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="$fristname $lastname"
                        txtborrowdetailID?.text="รหัส "+data.getString("BorrowDetailID")
                        txtdateStart?.text=data.getString("date_start")
                        txtemail?.text=data.getString("email")
                        txtphone?.text=data.getString("phone")
                        txtline?.text=data.getString("LineID")
                        txtPrinciple?.text="฿"+data.getString("Principle")
                        txtmoney_amount?.text="฿"+data.getString("remain")
                        txtinstullment_total?.text=data.getString("instullment_total")
                        txtinstullment_amont?.text=data.getString("instullment_Amount")
                        txtInterest?.text=data.getString("Interest")
                        txtInterest_penalty?.text=data.getString("Interest_penalty")
                        

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
    private fun showBill(borrowdetailID:String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_Bill_url)+borrowdetailID
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
                                    item.getString("historyDetailID"),
                                    item.getString("datepaying"),
                                    item.getString("date_check"),
                                    item.getString("money"),
                                    item.getString("money_total"),
                                    item.getString("imageBill"),
                                    item.getString("status"),
                                    item.getString("BorrowDetailID")




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
            var historyDetailID: String,var datepaying: String,var date_check: String,var money: String
            ,var money_total: String,var imageBill: String,var status:String,var BorrowDetailID:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loaner_bill,
                    parent, false
            )
            return ViewHolder(view)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txtdatebill.text="วันที่ "+ data.datepaying
            holder.txthistorybillid.text="รหัส "+data.historyDetailID
            holder.txtmoneybill.text="฿"+data.money_total
            if (data.status =="0"){
                holder.txtstatus.text="รอยืนยัน"
                holder.constraintLayout.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("historyDetailID", data.historyDetailID)
                    bundle.putString("BorrowDetailID", data.BorrowDetailID)
                    val fm = LoanerBillDetailFragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()
                }
            }
            if (data.status =="1"){
                holder.txtstatus.text="ยืนยันแล้ว"
                holder.txtstatus.setTextColor(Color.parseColor("#33BC40"));
            }




        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtstatus : TextView = itemView.findViewById(R.id.txtstatus)
            var txthistorybillid : TextView = itemView.findViewById(R.id.txtidbill)
            var txtmoneybill : TextView = itemView.findViewById(R.id.txtmoneybill)
            var txtdatebill : TextView = itemView.findViewById(R.id.txtdatebill)
            var constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayout)




        }
    }
}