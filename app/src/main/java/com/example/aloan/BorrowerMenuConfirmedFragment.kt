package com.example.aloan

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerMenuConfirmedFragment : Fragment() {
    var borrowerID:String?=null
    var txtWar:TextView?=null
    var recyclerView:RecyclerView?=null
    var back: ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_menu_confirmed, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)
        txtWar=root.findViewById(R.id.textView79)
        recyclerView=root.findViewById(R.id.recyclerView)
        back=root.findViewById(R.id.imageviewback)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerAccountFragment())
            fragmentTransaction.commit()
        }

       txtWar?.fadeIn()
        showlist()
        return root
    }
    inline fun View.fadeIn(durationMillis: Long = 1000) {
        this.startAnimation(AlphaAnimation(0F, 1F).apply {
            duration = durationMillis
            fillAfter = true
        })
    }
    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewConfirmed_url)+borrowerID
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
                                    item.getString("status"),
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
            var RequestID: String,var Money: String,var instullment: String,var Firstname: String
            ,var Lastname: String,var imageProfile: String,var dateRe:String,var borrowlistID:String,var LoanerID:String,
            var comment:String,var dateCheck:String,var status:String,var money_confirm:String,var instullment_confirm:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_borrower_cofrimed,
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

            holder.txtmoneyconfrim.text=data.money_confirm
            holder.txtinstallconfirm.text=data.instullment_confirm
            holder.con.setOnClickListener {
                val bundle = Bundle()
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
            var txtmoneyconfrim:TextView=itemView.findViewById(R.id.txtmoneyconfirm)
            var txtinstallconfirm:TextView=itemView.findViewById(R.id.txtinstallconfirm)




        }
    }
}