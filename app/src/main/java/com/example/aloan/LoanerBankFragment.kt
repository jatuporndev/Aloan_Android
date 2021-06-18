package com.example.aloan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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


class LoanerBankFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var swip:SwipeRefreshLayout?=null
    var btnaddbank:Button?=null
    var back:ImageView?=null
    var loanerID:String?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_bank, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        recyclerView=root.findViewById(R.id.recyclerView)
        swip=root.findViewById(R.id.swipe_layout)
        btnaddbank=root.findViewById(R.id.btnaddbank)
        back=root.findViewById(R.id.imageviewback)

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        btnaddbank?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerEditBankFragment())
            fragmentTransaction.commit()
        }
        swip?.setColorSchemeResources(
            R.color.maingree,
            R.color.maingree,
            R.color.maingree)

        swip?.setOnRefreshListener {
            showbank()
            swip?.isRefreshing=false
        }
        showbank()
        return root
    }
    private fun showbank() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_viewBank_url)+loanerID
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
                                item.getString("bankID"),
                                item.getString("bank"),
                                item.getString("holderName"),
                                item.getString("bankNumber"),
                                item.getString("imagebank")



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
        var bankID : String,var bank: String,var holderName: String,var bankNumber: String,var bankimg:String
    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_loaner_bank,
                parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txtbank.text=data.bank
            holder.txtholderName.text=data.holderName
            holder.txtbankID.text=data.bankNumber

            holder.btndelete.setOnClickListener {
                deleteBank(data.bankID)
                showbank()
            }
            var url = getString(R.string.root_url) +
                    getString(R.string.bank_image_url) + data.bankimg
            Picasso.get().load(url).into(holder.imagpro)



        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtholderName: TextView = itemView.findViewById(R.id.txtholdername)
            var txtbank: TextView = itemView.findViewById(R.id.txtbankname)
            var txtbankID: TextView = itemView.findViewById(R.id.txtbankID)
            var btndelete: ImageButton = itemView.findViewById(R.id.imageButtonDelete)
            var imagpro:ImageView=itemView.findViewById(R.id.imgpro)

        }
    }
    private fun deleteBank(BankID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_deleteBank_url) + BankID
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
}