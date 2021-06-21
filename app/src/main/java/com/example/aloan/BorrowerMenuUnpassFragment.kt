package com.example.aloan

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerMenuUnpassFragment : Fragment() {
    var borrowerID:String?=null
    var recyclerView:RecyclerView?=null
    var back:ImageView?=null
    var wsipe: SwipeRefreshLayout?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_menu_unpass, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)
        recyclerView=root.findViewById(R.id.recyclerView)
        back=root.findViewById(R.id.imageviewback)

        wsipe=root.findViewById(R.id.swipe_layout)
        wsipe?.setColorSchemeResources(
                R.color.mainor,
                R.color.mainor,
                R.color.mainor)

        wsipe?.setOnRefreshListener {
            showlist()
            wsipe?.isRefreshing=false
        }

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerAccountFragment())
            fragmentTransaction.commit()
        }

        showlist()
        return root
    }

    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewunpass_url)+"?BorrowerID="+borrowerID
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
                                item.getString("LoanerID"),
                                item.getString("comment"),
                                item.getString("dateCheck"),
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
        var RequestID: String,var Money: String,var instullment: String,var Firstname: String
        ,var Lastname: String,var imageProfile: String,var dateRe:String,var borrowlistID:String,var LoanerID:String,
        var comment:String,var dateCheck:String,var status:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_borrower_unpass,
                parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.profileBLoaner_image_url) + data.imageProfile
            Picasso.get().load(url).into(holder.imageProfile)
            holder.nameLoaner.text="คุณ ${data.Firstname} ${data.Lastname}"
            holder.money.text="฿"+data.Money
            holder.txtdate.text=data.dateRe
            holder.txtinstall.text=data.instullment
            holder.txtdateCheck.text=data.dateCheck

            holder.btncheck.setOnClickListener {
                updateUnpassCheck(data.RequestID)
                val builder = AlertDialog.Builder(requireContext())

                builder.setTitle("สาเหตุของการกู้ไม่ผ่าน")
                builder.setMessage(data.comment)

                builder.setPositiveButton("YES"){dialog, which ->
                    showlist()
                }

                val dialog: AlertDialog = builder.create()

                dialog.show()

            }
            if(data.status =="4") {
                holder.con.setBackgroundColor(Color.parseColor("#FFF5E9"))
            }

            holder.con.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("borrowlistID", data.borrowlistID)
                bundle.putString("LoanerID",data.LoanerID )
                bundle.putString("backlist","unpass")
                val fm = BorrowDetailListFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
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
            var txtdateCheck:TextView=itemView.findViewById(R.id.txtdate2)
            var imageProfile :ImageView = itemView.findViewById(R.id.imgpro)
            var btncheck: Button =itemView.findViewById(R.id.btncant)
            var con: ConstraintLayout =itemView.findViewById(R.id.consta)



        }
    }
    private fun updateUnpassCheck(RequrstID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updateUnpassChecked_url) + RequrstID
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
}