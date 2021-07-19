package com.example.aloan

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class BorrowerMenuPayingDetailFragment : Fragment() {

    var back:ImageView?=null
    var imgpro:ImageView?=null
    var txtborrowdetailID:TextView?=null
    var txtname:TextView?=null
    var txtdateStart:TextView?=null
    var txtphone:TextView?=null
    var txtemail:TextView?=null
    var txtline:TextView?=null
    var txtPrinciple:TextView?=null
    var txtinstullment_total:TextView?=null
    var txtInterest:TextView?=null
    var txtInterest_penalty:TextView?=null
    var txtmoney_amount:TextView?=null
    var txtinstullment_amont:TextView?=null
    var recyclerView:RecyclerView?=null
    var txttotalmoney:TextView?=null
    var btnpayment:Button?=null
    var txthis:TextView?=null
    var txtslip:TextView?=null
    var imgslipLoaner:ImageView?=null
    var txtmoney_total:TextView?=null

    var monneytotal:Float= 0F
    var monneytoRemain:Float= 0F
    var borrowDetailID:String?=null
    var loanerID:String=""
    private var IDhis = java.util.ArrayList<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_menu_paying_detail, container, false)
        val bundle =this.arguments
        borrowDetailID=bundle?.get("borrowDetailID").toString()
        recyclerView=root.findViewById(R.id.recyclerView2)
        back=root.findViewById(R.id.imageViewback)
        imgpro=root.findViewById(R.id.imgpro)
        txtborrowdetailID=root.findViewById(R.id.textborrowdetailID)
        txtdateStart=root.findViewById(R.id.txtdayReB)
        txtname=root.findViewById(R.id.txtnameB)
        txtphone=root.findViewById(R.id.txtPhoneB)
        txtemail=root.findViewById(R.id.txtemailB)
        txtline=root.findViewById(R.id.txtlineB)

        txtPrinciple=root.findViewById(R.id.txtPrinciple)
        txtinstullment_total=root.findViewById(R.id.txtinstullment_total)
        txtInterest=root.findViewById(R.id.txtInterest)
        txtinstullment_amont=root.findViewById(R.id.txtinstullment_amont)
        txtInterest_penalty=root.findViewById(R.id.txtInterest_penalty)
        txttotalmoney=root.findViewById(R.id.txttotalmoney)
        txtmoney_amount=root.findViewById(R.id.txtmoney_amount)
        btnpayment=root.findViewById(R.id.btnpass)
        txthis=root.findViewById(R.id.txthis)
        txtslip=root.findViewById(R.id.txtslip)
        txtmoney_total=root.findViewById(R.id.txtmoney_total)

        txthis?.paintFlags = txthis?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!
        txtslip?.paintFlags = txthis?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!

        IDhis?.clear()

        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, BorrowerMenuPayingFragment())
            fragmentTransaction.commit()
        }



        btnpayment?.setOnClickListener {

            if(monneytotal==0f){
                Toast.makeText(context, "เลือกรายการชำระเงิน", Toast.LENGTH_LONG).show()
            }else{


            val bundle = Bundle()
           // bundle.putString("criterionID", data.criterionID)
            bundle.putStringArrayList("arrayID",IDhis)
            bundle.putString("moneytotal",monneytotal.toString())
            bundle.putString("moneytoRemain",monneytoRemain.toString())
            bundle.putString("borrowDetailID",borrowDetailID)
            bundle.putString("loanerID",loanerID)
            val fm = BorrowerPaymentFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
            }
        }
        txthis?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("BorrowDetailID", borrowDetailID)
            val fm = BorrowerHistoryBillFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

        var view:View = layoutInflater.inflate(R.layout.r_slip,null)
        imgslipLoaner = view.findViewById(R.id.imgslip1)
        var btnclose:ImageView=view.findViewById(R.id.imageviewback)
        var dialog: Dialog = Dialog(requireContext(),android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight)
        dialog.setContentView(view)

        txtslip?.setOnClickListener {
            dialog.show()
        }
        btnclose.setOnClickListener {
            dialog.dismiss()
        }


        viewdetail(borrowDetailID!!)
        return root
    }
    @SuppressLint("SetTextI18n")
    private fun viewdetail(BorrowDetailID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.ManuPaydetail_url) + BorrowDetailID
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
                        var fristname =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="$fristname $lastname"
                        txtborrowdetailID?.text="รหัส "+data.getString("BorrowDetailID")
                        txtdateStart?.text=data.getString("date_start")
                        txtemail?.text=data.getString("email")
                        txtphone?.text=data.getString("phone")
                        txtline?.text=data.getString("LineID")
                        txtPrinciple?.text="฿"+data.getString("Principle")
                        txtmoney_amount?.text="฿"+data.getString("remain")
                        txtinstullment_total?.text=data.getString("instullment_total")
                        txtinstullment_amont?.text=data.getString("instullment_Amount")
                        txtInterest?.text=data.getString("Interest")
                        txtInterest_penalty?.text=data.getString("Interest_penalty")
                        txtmoney_total?.text="฿"+data.getString("total")
                        loanerID=data.getString("LoanerID")

                        viewPaying(data.getString("BorrowDetailID"))

                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBLoaner_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

                        var url2 = getString(R.string.root_url) +
                                getString(R.string.Loanerslip_image_url) + data.getString("receipt_slip")
                        Picasso.get().load(url2).into(imgslipLoaner)

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

    private fun viewPaying(BorrowDetailID: String) {

        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.ViewPaying_url)+BorrowDetailID
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
                          //  if(item.getString("dateset_status")=="1"){
                           //     monneytotal += ( (item.getString("moneySet").toFloat()) + (item.getString("interest_penalty_money").toFloat()) )
                          //  }
                          //  if(item.getString("dateset_status")=="0"){
                          //      monneytotal += ( (item.getString("moneySet").toFloat()) )
                          //  }

                            recyclerView!!.adapter = DataAdapter(data)

                        }
                     //   txttotalmoney?.text=monneytotal.toString()
                    } else {
                        recyclerView!!.adapter = DataAdapter(data)
                        monneytotal= 0F
                        txttotalmoney?.text=monneytotal.toString()

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

            if (data.dateset_status=="0") {
                if (holder.checkBox.isChecked) {
                    monneytotal += data.moneySet.toFloat()
                    monneytoRemain += data.moneySet.toFloat()
                    IDhis.add(data.HistoryID)
                }
            }
            if (data.dateset_status=="1") {
                if (holder.checkBox.isChecked) {
                    monneytotal+=data.moneySet.toFloat()+data.interest_penalty_money.toFloat()
                    monneytoRemain += data.moneySet.toFloat()
                    IDhis.add(data.HistoryID)
                }
            }

            holder.checkBox.setOnClickListener {

                if (holder.checkBox.isChecked) {
                    monneytotal += data.moneySet.toFloat()
                    monneytoRemain += data.moneySet.toFloat()
                    IDhis.add(data.HistoryID)
                }else{
                    monneytotal -= data.moneySet.toFloat()
                    monneytoRemain -= data.moneySet.toFloat()
                    IDhis.remove(data.HistoryID)
                }
                txttotalmoney?.text=String.format("%.2f", monneytotal)
            }

            txttotalmoney?.text=String.format("%.2f", monneytotal)


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
            var checkBox:CheckBox=itemView.findViewById(R.id.checkBox4)
            var txterror:TextView=itemView.findViewById(R.id.textView101)



        }
    }

}