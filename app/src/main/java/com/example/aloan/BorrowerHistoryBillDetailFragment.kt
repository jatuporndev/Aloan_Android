package com.example.aloan

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerHistoryBillDetailFragment : Fragment() {

    var txtHistoryBillID:TextView?=null
    var recyclerView:RecyclerView?=null
    var back:ImageView?=null
    var imgbill:ImageView?=null
    var txtdate:TextView?=null
    var txtTotalmoney:TextView?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_history_bill_detail, container, false)
        txtHistoryBillID = root.findViewById(R.id.txthistorybillID)
        recyclerView=root.findViewById(R.id.recyclerView)
        imgbill=root.findViewById(R.id.imgslip)
        back=root.findViewById(R.id.imageviewback)
        txtdate=root.findViewById(R.id.txtdatehis2)
        txtTotalmoney=root.findViewById(R.id.txttotalmoneyhis)

        val Bundle=this.arguments
        var historyDetailID = Bundle?.get("historybillID").toString()
        var borrowDetailID= Bundle?.get("borrowDetailID").toString()
        viewdetail(historyDetailID)
        viewPaying(historyDetailID)

        Log.d("test23",borrowDetailID)

        back?.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("BorrowDetailID",borrowDetailID)
            val fm = BorrowerHistoryBillFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }



        return root
    }
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

                        txtHistoryBillID?.text="รหัส "+data.getString("historyDetailID")
                        txtdate?.text=data.getString("datepaying")
                        txtTotalmoney?.text="฿"+data.getString("money_total")
                        //txtmoneyfire?.text="฿"+data.getString("fire")


                        var url = getString(R.string.root_url) +
                                getString(R.string.payment_image_url) + data.getString("imageBill")
                        Picasso.get().load(url).into(imgbill)

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
}