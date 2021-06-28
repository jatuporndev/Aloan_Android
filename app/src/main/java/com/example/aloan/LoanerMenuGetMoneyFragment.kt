package com.example.aloan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerMenuGetMoneyFragment : Fragment() {

    var recyclerView:RecyclerView?=null
    var back:ImageView?=null
    var swip:SwipeRefreshLayout?=null
    var loanerID:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_menu_get_money, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)

        recyclerView=root.findViewById(R.id.recyclerView)
        back=root.findViewById(R.id.imageViewback)
        swip=root.findViewById(R.id.swipe_layout)

        showlist()
        return root
    }
    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_ViewBorrowDetail_url)+loanerID
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
                                    item.getString("BorrowDetailID"),
                                    item.getString("date_start"),
                                    item.getString("Update_date"),
                                    item.getString("Principle"),
                                    item.getString("remain"),
                                    item.getString("instullment_total"),
                                    item.getString("imageProfile"),
                                    item.getString("firstname"),
                                    item.getString("lastname")




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
            var BorrowDetailID: String,var date_start: String,var Update_date: String,var Principle: String
            ,var remain: String,var instullment_total: String,var imageProfile:String,var firstname:String,var lastname:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loaner_waitpay,
                    parent, false
            )
            return ViewHolder(view)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.profileBorrower_image_url) + data.imageProfile
            Picasso.get().load(url).into(holder.imageProfile)

            holder.nameLoaner.text="คุณ ${data.firstname} ${data.lastname}"
            holder.borrowdetailID.text=data.BorrowDetailID
            holder.money.text=data.Principle
            holder.txtinstall.text=data.instullment_total
            holder.txtdate.text=data.date_start
            holder.txtdatenext.text=nextday(data.BorrowDetailID)
            holder.moneyper.text=(data.remain.toFloat()/data.instullment_total.toFloat()).toString()

            if (checkpay(data.BorrowDetailID)){
                holder.txttsttus.text="มีการชำระใหม่รอยืนยัน"
                bl(holder.txttsttus)
            }

            holder.btncheck.setOnClickListener {

            }
            holder.con.setOnClickListener {

            }


        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var nameLoaner : TextView = itemView.findViewById(R.id.txtnameW)
            var money : TextView = itemView.findViewById(R.id.txtmoneyre)
            var txtinstall : TextView = itemView.findViewById(R.id.txtinstall)
            var txtdate : TextView = itemView.findViewById(R.id.txtdate)
            var txtdatenext : TextView = itemView.findViewById(R.id.txtdatenext)
            var imageProfile :ImageView = itemView.findViewById(R.id.imgpro)
            var btncheck: Button =itemView.findViewById(R.id.btncant)
            var con: ConstraintLayout =itemView.findViewById(R.id.consta)
            var borrowdetailID: TextView =itemView.findViewById(R.id.textView93)
            var moneyper: TextView =itemView.findViewById(R.id.txtmoneyconfirm)
            var txttsttus:TextView=itemView.findViewById(R.id.textView107)



        }
    }
    private fun nextday(BorrowDetailID:String):String{
        var settlement_date=""
        var url: String = getString(R.string.root_url) + getString(R.string.nextDateurl) + BorrowDetailID
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
                        settlement_date =data.getString("settlement_date")

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
        return settlement_date
    }
    private fun checkpay(BorrowDetailID:String):Boolean{
        var settlement_date=false
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_checkpayMenu_url) + BorrowDetailID
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
                        //////////////////////////
                        settlement_date=true
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
        return settlement_date
    }
    fun bl(text:TextView){
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500 //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        text.startAnimation(anim)
    }
}