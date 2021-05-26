package com.example.aloan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class BorrowerPinedFragment : Fragment() {

    var recyclerView:RecyclerView?=null
    var borrowerID:String?=null
    var back:ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_pined, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)

        recyclerView =root.findViewById(R.id.recyclerView)
        back=root.findViewById(R.id.imageviewback)

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
        val url: String = getString(R.string.root_url) + getString(R.string.viewpined_url)+borrowerID
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
                                    item.getString("borrowlistID"),
                                    item.getString("money_min"),
                                    item.getString("money_max"),
                                    item.getString("interest"),
                                    item.getString("firstname"),
                                    item.getString("lastname"),
                                    item.getString("LoanerID"),
                                    item.getString("imageProfile")


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
            var borrowlistID: String,var money_min: String,var money_max: String,var interest: String,
            var Firstname: String,var Lastname: String,var LoanerID: String,var imageProfile: String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_borrowlist,
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
            holder.money.text=data.money_min+" ~ "+data.money_max+"฿"
            holder.interest.text=data.interest+"%"
            holder.name.text="คุณ "+data.Firstname+" "+data.Lastname

            holder.con.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("borrowlistID", data.borrowlistID)
                bundle.putString("LoanerID",data.LoanerID )
                bundle.putString("backlist","pin")

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
            var money: TextView = itemView.findViewById(R.id.txtmoney)
            var interest: TextView = itemView.findViewById(R.id.txtinte)
            var name: TextView = itemView.findViewById(R.id.txtnamel)
            var imageProfile: ImageView = itemView.findViewById(R.id.imgpro)
            var con: ConstraintLayout =itemView.findViewById(R.id.consta)


        }
    }

}