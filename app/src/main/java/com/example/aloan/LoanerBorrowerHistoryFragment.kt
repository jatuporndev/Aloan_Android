package com.example.aloan

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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


class LoanerBorrowerHistoryFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var back:ImageView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_borrower_history, container, false)
        val bundle = this.arguments
        var requestID = bundle?.get("RequestID")
        recyclerView=root.findViewById(R.id.recyclerView)
        back=root.findViewById(R.id.imageviewback)

        back?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("RequestID", requestID.toString())
            val fm = LoanerMenuRequestDetailFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        showlist(bundle?.get("BorrowerID").toString())
        return root
    }

    private fun showlist(borrowerID:String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_indexHistoryurl)+borrowerID
        Log.d("testu",url)
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
                                item.getString("firstname"),
                                item.getString("lastname"),
                                item.getString("Principle"),
                                item.getString("instullment_total"),
                                item.getString("date_start"),
                                item.getString("Status")

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
        var firstname: String,var lastname: String,var Principle: String,var instullment_total: String
        ,var date_start: String,var Status: String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_loaner_borrowerhistory,
                parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.name.text="คุณ ${data.firstname} ${data.lastname}"
            holder.money.text="฿"+data.Principle
            holder.txtinstall.text=data.instullment_total
            holder.txtdate.text=data.date_start

            if(data.Status=="0"){
                holder.txtstatus.text="อยู่ระหว่างชำระ"
            }
            if(data.Status=="1"){
                holder.txtstatus.text="สำเร็จ"
                holder.txtstatus.setTextColor(Color.parseColor("#33BC40"));
            }

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var name : TextView = itemView.findViewById(R.id.txtnameborrowerhis)
            var money : TextView = itemView.findViewById(R.id.txtmoneyhis)
            var txtinstall : TextView = itemView.findViewById(R.id.txtinthis)
            var txtdate:TextView=itemView.findViewById(R.id.txtdatestarthis)
            var txtstatus: TextView =itemView.findViewById(R.id.txtstatus1)


        }
    }
}