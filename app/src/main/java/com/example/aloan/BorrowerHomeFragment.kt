package com.example.aloan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BorrowerHomeFragment : Fragment() {
        var con:ConstraintLayout?=null
        var txtfilter:TextView?=null
        var spinmoney:Spinner?=null
        var spininterest:Spinner?=null
        var moneyU=""
        var interestU=""
        var search=""
        var searchview:SearchView?=null
        var recyclerView:RecyclerView?=null
        private var money = java.util.ArrayList<Money>()
        private var interest = java.util.ArrayList<Interest>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_borrower_home, container, false)
        con = root.findViewById(R.id.constraintLayout)
        txtfilter =root.findViewById(R.id.txtfilter)
        spinmoney=root.findViewById(R.id.spinnermoney)
        spininterest=root.findViewById(R.id.spinnerinterest)
        recyclerView=root.findViewById(R.id.recyclerView)
        searchview=root.findViewById(R.id.searchview)
        con?.visibility=View.GONE
        txtfilter?.setOnClickListener {
            if (con?.visibility==View.GONE){
                con?.visibility=View.VISIBLE
                txtfilter?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_upward_24, 0);
            }else{
                con?.visibility=View.GONE
                txtfilter?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_downward_24, 0);
            }
        }

        money.add(Money("ทั้งหมด",""))
        money.add(Money("มากกว่า 5,000","5000"))
        money.add(Money("มากกว่า 10,000","10000"))
        money.add(Money("มากกว่า 50,000","50000"))
        money.add(Money("มากกว่า 100,000","100000"))
        money.add(Money("มากกว่า 1,000,000","1000000"))
        val adapterband = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, money)
        spinmoney?.adapter = adapterband
        spinmoney?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = spinmoney!!.selectedItem as Money
                moneyU = band.moneynum
                showlist()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        interest.add(Interest("ทั้งหมด",""))
        interest.add(Interest("ไม่เกิน 5%","5"))
        interest.add(Interest("ไม่เกิน 8%","8"))
        interest.add(Interest("ไม่เกิน 13%","13"))


        val adapterinterest = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, interest)
        spininterest?.adapter = adapterinterest
        spininterest?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = spininterest!!.selectedItem as Interest
                interestU = band.interestnum
                showlist()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        searchview?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
              search=query
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                search=newText
                showlist()
                return false
            }
        })


        showlist()
        return root
    }

    class Money(var moneyText: String,var moneynum: String) {
        override fun toString(): String {
            return moneyText
        }
    }
    
    class Interest(var interestText: String,var interestnum: String) {
        override fun toString(): String {
            return interestText

        }
    }
    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.showlist_url)+"?money_max="+moneyU+"&interest="+interestU+"&search="+search
        Log.d("testt",url)
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
                          //  recyclerView?.setHasFixedSize(true);
                           // recyclerView?.addItemDecoration(DividerItemDecoration(requireContext(),
                              //      DividerItemDecoration.HORIZONTAL))
                           // recyclerView?.addItemDecoration( DividerItemDecoration(requireContext(),
                           //         DividerItemDecoration.VERTICAL))
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
                bundle.putString("backlist","home")

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
            var con:ConstraintLayout=itemView.findViewById(R.id.consta)


        }
    }


}