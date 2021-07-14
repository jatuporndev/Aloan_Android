package com.example.aloan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerHistoryBillFragment : Fragment() {

    var back:ImageView?=null
    var recyclerView:RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =inflater.inflate(R.layout.fragment_borrower_history_bill, container, false)
        val bundle=this.arguments
        val borrowDetailID =bundle?.get("BorrowDetailID").toString()
        back=root.findViewById(R.id.imageviewback)
        recyclerView=root.findViewById(R.id.recyclerView)

        back?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("borrowDetailID", borrowDetailID)
            val fm = BorrowerMenuPayingDetailFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

        showlist(borrowDetailID)

        return root
    }
    private fun showlist(BorrowDetailID:String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.historybill_url)+BorrowDetailID
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
                                item.getString("money_total"),
                                item.getString("status")



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
        var historybillID: String,var datepaying: String,var money_total: String,var status: String


    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_borrower_history_bill,
                parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txtdate.text=data.datepaying
            holder.txtmoney.text="฿"+data.money_total
            holder.txtID.text="รหัสการชำระเงิน "+data.historybillID

            if(data.status =="0"){
                holder.txtstatus.text="รอยืนยัน"
                holder.txtstatus.setTextColor(Color.parseColor("#FF612F"))
            }
            if(data.status=="1"){
                holder.txtstatus.text="ยืนยันแล้ว"
            }

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtdate:TextView=itemView.findViewById(R.id.txtdatebill1)
            var txtmoney:TextView=itemView.findViewById(R.id.txtmoneybill1)
            var txtstatus:TextView=itemView.findViewById(R.id.txtstatusbill1)
            var txtID:TextView=itemView.findViewById(R.id.textView128)
            var con: ConstraintLayout =itemView.findViewById(R.id.constraintlayout)



        }
    }

}