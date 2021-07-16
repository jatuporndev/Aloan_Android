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
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerMenuWaitingFragment : Fragment() {
    var borrowerID:String?=null
    var recyclerView:RecyclerView?=null

    var back :ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root = inflater.inflate(R.layout.fragment_borrower_menu_waiting, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginBorrowerActivity().appPreference, Context.MODE_PRIVATE)
        borrowerID = sharedPrefer?.getString(LoginBorrowerActivity().borrowerIdPreference, null)

        back =root.findViewById(R.id.imageviewback)
        recyclerView=root.findViewById(R.id.recyclerView)

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
        val url: String = getString(R.string.root_url) + getString(R.string.viewrequest_url)+"?BorrowerID="+borrowerID
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
            var RequestID: String,var Money: String,var instullment: String,var Firstname: String
            ,var Lastname: String,var imageProfile: String,var dateRe:String,var borrowlistID:String,var LoanerID:String,var status:String
            ,var dateCheck:String,var money_confirm:String,var instullment_confirm:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_borrower_waiting,
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
            holder.txtdate.text="วันที่ส่งคำขอ: "+data.dateRe
            holder.txtinstall.text=data.instullment

            if(data.status=="2"){
                holder.txtstatus.text="รอผู้ให้กู้โอนเงิน"
                holder.txtstatus.setTextColor(Color.parseColor("#33BC40"));
                holder.money.setTextColor(Color.parseColor("#33BC40"));
                holder.txtinstall.setTextColor(Color.parseColor("#33BC40"));
                holder.btncancel.visibility= View.GONE
                holder.txtdate.text="วันที่ยืนยัน: "+data.dateCheck
                holder.money.text="฿"+data.money_confirm
                holder.txtinstall.text=data.instullment_confirm
                holder.con.isEnabled=false
            }

            holder.btncancel.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("แจ้งเตือน")
                builder.setMessage("ยืนยันการที่จะยกเลิกคำขอหรือไม่?")
                        .setCancelable(false)
                        .setPositiveButton("ยืนยัน") { dialog, id ->
                            // ใช่
                            //deleterequest(data.RequestID)
                            Cancel(data.RequestID)
                            showlist()
                        }
                        .setNegativeButton("ยกเลิก") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()

            }
            holder.con.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("borrowlistID", data.borrowlistID)
                bundle.putString("LoanerID",data.LoanerID )
                bundle.putString("backlist","waiting")
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
            var nameLoaner :TextView = itemView.findViewById(R.id.txtnameW)
            var money :TextView = itemView.findViewById(R.id.txtmoneyre)
            var txtinstall :TextView = itemView.findViewById(R.id.txtinstall)
            var txtdate :TextView = itemView.findViewById(R.id.dateendg)
            var imageProfile :ImageView = itemView.findViewById(R.id.imgpro)
            var btncancel:Button =itemView.findViewById(R.id.btncant)
            var txtstatus:TextView=itemView.findViewById(R.id.textView66)
            var con:ConstraintLayout=itemView.findViewById(R.id.consta)


        }
    }

    private fun deleterequest(RequestID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.deleterequest_url) + RequestID
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
    private fun Cancel(RequrstID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_updateUnpass_url) + RequrstID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("comment", "ยกเลิก")
                .build()
        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuUnpassFragment())
                    fragmentTransaction.commit()
                    Toast.makeText(requireContext(), "\n ยกเลิกสำเร็จ", Toast.LENGTH_LONG).show()

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