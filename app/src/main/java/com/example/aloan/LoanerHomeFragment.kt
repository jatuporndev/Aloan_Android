package com.example.aloan

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerHomeFragment : Fragment() {
    var recyclerView:RecyclerView?=null
   // var btnadd:Button?=null
    var loanerID: String? = null
    var txtmoneyMax:TextView?=null
    var txtmoneyMin:TextView?=null
    var txtinterest:TextView?=null
    var txtinterest_penalty:TextView?=null
    var txtinstullment_max:TextView?=null
    var btnedit:Button?=null
    var switchPublic:Switch?=null
    var txtname:TextView?=null
    var borrowlistID=""
    var cri="";
    var txtcountrequest:TextView?=null
    var txtcountpay:TextView?=null
    var lastFirstVisiblePosition = -1
    var sc: NestedScrollView?=null

    var moneyMax:String?=null
    var instullmentMax:String?=null

    var index = -1
    var top = -1
    var mLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val root =inflater.inflate(R.layout.fragment_loaner_home, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        val bundle = this.arguments

        sc=root.findViewById(R.id.NestedScrollView)
        recyclerView=root.findViewById(R.id.recyclerView)
        txtmoneyMax=root.findViewById(R.id.txtmoneyMax)
        txtmoneyMin=root.findViewById(R.id.txtmoneyRequestB)
        txtinterest=root.findViewById(R.id.txtiInterest_requestB)
        txtinterest_penalty=root.findViewById(R.id.txtInterest_penalty_requestB)
        btnedit=root.findViewById(R.id.btncancel)
        switchPublic=root.findViewById(R.id.switchPublic)
        txtname=root.findViewById(R.id.txtname)
        txtcountrequest=root.findViewById(R.id.txtcountrequest)
        txtcountpay=root.findViewById(R.id.txtcountpay)
        txtinstullment_max=root.findViewById(R.id.txtintu)

        mLayoutManager = LinearLayoutManager(requireContext());
        recyclerView?.setHasFixedSize(true);
        recyclerView?.layoutManager = mLayoutManager;


        btnedit?.setOnClickListener {
            val bundle = Bundle()
            val fm = LoanerBorrowlistEditFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

        Handler().postDelayed({

            if(bundle?.get("scroll").toString() !="null") {
                sc?.scrollTo(0, bundle?.get("scroll").toString().toInt())
            }
        }, 0)

        if (bundle?.get("index").toString()!="null"){
            index=bundle?.get("index").toString().toInt()
        }
        viewlist(loanerID)
        showCriterion(borrowlistID)
        if (cri=="0"){
            addCriterion(borrowlistID)
        }



        return root
    }


    private fun viewlist(userID: String?) {

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_showborrowlist_url) + userID
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

                         txtmoneyMax?.text = data.getString("money_max")+"฿"
                        moneyMax=data.getString("money_max")
                         txtmoneyMin?.text = data.getString("money_min")+"฿"
                        txtinterest?.text = data.getString("interest")+"%"
                        txtinterest_penalty?.text = data.getString("Interest_penalty")+"%"
                        var status = data.getString("status")
                        var name =data.getString("firstname")
                        var lastname =data.getString("lastname")
                        txtname?.text="สวัสดี $name $lastname"
                        switchPublic?.isChecked = status=="1"
                        borrowlistID=data.getString("borrowlistID")
                        txtinstullment_max?.text=data.getString("instullment_max")
                        instullmentMax=data.getString("instullment_max")
                        switchPublic?.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                // The switch enabled
                                if(cri=="1"){
                                    setpublic("1")
                                }else{
                                    Toast.makeText(requireContext(), "เพิ่มเกณฑ์การให้กู้อย่างน้อย 1 รายการ", Toast.LENGTH_LONG).show()
                                    switchPublic?.isChecked=false

                                }

                            } else {
                                setpublic("0")


                            }
                        }
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
    fun setpublic(status:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_setpublic_url)+loanerID+","+status
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


    private fun showCriterion(borrowlistID: String) {

        if (index != -1){
            mLayoutManager?.scrollToPositionWithOffset( index, 0);
        }

        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.loaner_showCriterion_url)+borrowlistID
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
                                    item.getString("criterionID"),
                                    item.getString("Age_range"),
                                    item.getString("Saraly_range"),
                                    item.getString("Married"),
                                    item.getString("money_max"),
                                    item.getString("borrowlistID"),
                                    item.getString("instullment_max"),
                                    item.getString("edit")



                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)
                            cri ="1"
                            //(recyclerView!!.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastFirstVisiblePosition, 0)

                        }
                    } else {
                        recyclerView!!.adapter = DataAdapter(data)
                        cri ="0"
                        setpublic("0")
                        viewlist(loanerID)


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
            var criterionID: String,var Age_range: String,var Saraly_range: String,var Married: String,
            var money_max: String,var borrowlistID: String,var instullment_max: String,var edit:String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_criterion,
                    parent, false

            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var arrayAge = arrayOf("18-28 ปี","29-39 ปี","40-50 ปี","51ปีขึ้นไป")
            var arrayMarri = arrayOf("โสด","แต่งงานแล้ว")
            var arraySalary = arrayOf("0-9000","9000-15000","15000-50000","มากกว่า5หมื่น")

            val data = list[position]
            holder.data = data
            holder.brndelete.setOnClickListener {

                index = mLayoutManager?.findFirstVisibleItemPosition()!!
                val v: View? = recyclerView?.getChildAt(0)
                top = if (v == null) 0 else (v.top - (recyclerView?.paddingTop!!))

                val bundle = Bundle()
                bundle.putString("criterionID", data.criterionID)
                bundle.putString("sc", sc?.scrollY.toString())
                bundle.putString("index", index.toString())

                bundle.putString("instullmentMax", instullmentMax)
                bundle.putString("moneyMax", moneyMax)

                val fm = LoanerEditCriterionFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }

            holder.txtageR.text=arrayAge[data.Age_range.toInt()]
            holder.txtmarriR.text=arrayMarri[data.Married.toInt()]
            holder.txtsalaryR.text=arraySalary[data.Saraly_range.toInt()]
            holder.txtmoneyR.text=data.money_max+"฿"
            holder.txtinstullment_max.text=data.instullment_max
            bl(holder.txtnew)
            if (data.edit=="1") {
                holder.txtnew.visibility = View.GONE
            }

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtageR: TextView = itemView.findViewById(R.id.txtageR)
            var txtsalaryR: TextView = itemView.findViewById(R.id.txtsalaryR)
            var txtmarriR: TextView = itemView.findViewById(R.id.txtmarriR)
            var txtmoneyR: TextView = itemView.findViewById(R.id.txtmoneyR)
            var brndelete:ImageButton= itemView.findViewById(R.id.imageButtondelete)
            var txtinstullment_max:TextView=itemView.findViewById(R.id.txtinstu)
            var txtnew:TextView=itemView.findViewById(R.id.txtnew)


        }
    }
    private fun deletecri(criterionID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_deleteCriterion_url) + criterionID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .delete()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    showCriterion(borrowlistID)

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

    fun bl(text:TextView){
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500 //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        text.startAnimation(anim)
    }

    private fun addCriterion(borrowlist:String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_insertCriterion_url)+borrowlist
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
                    showCriterion(borrowlistID)
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