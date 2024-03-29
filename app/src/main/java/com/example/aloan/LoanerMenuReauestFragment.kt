package com.example.aloan

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerMenuReauestFragment : Fragment() {

    var loanerID:String?=null
    var recyclerView: RecyclerView?=null
    var wsipe: SwipeRefreshLayout?=null

    var back : ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root =inflater.inflate(R.layout.fragment_loaner_menu_reauest, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        back =root.findViewById(R.id.imageviewback)
        recyclerView=root.findViewById(R.id.recyclerView)
        wsipe=root.findViewById(R.id.swipe_layout)
        wsipe?.setColorSchemeResources(
            R.color.maingree,
            R.color.maingree,
            R.color.maingree)

        wsipe?.setOnRefreshListener {
            showlist()
            wsipe?.isRefreshing=false
        }

        back?.setOnClickListener {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        showlist()
        return root
    }

    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_Menu1Request_url)+loanerID
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
                                    item.getString("RequestID"),
                                    item.getString("Money"),
                                    item.getString("instullment_request"),
                                    item.getString("firstname"),
                                    item.getString("lastname"),
                                    item.getString("imageProfile"),
                                    item.getString("dateRe"),
                                    item.getString("borrowlistID"),
                                    item.getString("status"),
                                    item.getString("dateCheck"),
                                    item.getString("money_confirm"),
                                    item.getString("instullment_confirm")

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
            var RequestID: String,var Money: String,var instullment_request: String,var Firstname: String
            ,var Lastname: String,var imageProfile: String,var dateRe:String,var borrowlistID:String
            ,var status:String,var dateCheck:String,var money_confirm:String,var instullment_confirm:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loaner_request,
                    parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.profileBorrower_image_url) + data.imageProfile
            Picasso.get().load(url).into(holder.imageProfile)
            holder.nameLoaner.text="คุณ ${data.Firstname} ${data.Lastname}"
            holder.money.text="฿"+data.Money
            holder.txtdate.text=data.dateRe
            holder.txtinstall.text=data.instullment_request

            if(data.status=="1"){
                holder.btnview.visibility=View.INVISIBLE
                holder.money.text="฿"+data.money_confirm
                holder.txtdate.text="วันที่ยืนยัน: "+data.dateCheck
                holder.txtinstall.text=data.instullment_confirm
                holder.txtstatus.text="รอผู้กู้ยอมรับ"
                holder.txtstatus.setTextColor(Color.parseColor("#FF612F"));
                holder.money.setTextColor(Color.parseColor("#FF612F"));
                holder.txtinstall.setTextColor(Color.parseColor("#FF612F"));
            }

            holder.btnview.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("RequestID", data.RequestID)
                val fm = LoanerMenuRequestDetailFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }

            if(data.status=="0") {
                holder.con.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("RequestID", data.RequestID)

                    val fm = LoanerMenuRequestDetailFragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()
                }
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
            var txtdate : TextView = itemView.findViewById(R.id.dateendg)
            var imageProfile :ImageView = itemView.findViewById(R.id.imgpro)
            var btnview: Button =itemView.findViewById(R.id.btncant)
            var con: ConstraintLayout =itemView.findViewById(R.id.consta)
            var txtstatus:TextView=itemView.findViewById(R.id.textView66)


        }
    }
}