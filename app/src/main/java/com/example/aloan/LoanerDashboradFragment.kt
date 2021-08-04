package com.example.aloan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat


class LoanerDashboradFragment : Fragment() {
    var back: ImageView? = null
    var year: Spinner? = null
    var mount: Spinner? = null

    var money: TextView? = null
    var txtPrinciple: TextView? = null
    var txtMoneyGet: TextView? = null
    var loanerID: String? = null
    var spineryear = java.util.ArrayList<String>()
    var spinermount = java.util.ArrayList<dataMonth>()


    var monthlySale: BarChart? = null
    var yearText: String? = null
    var monthText: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loaner_dashborad, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE
        )
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)
        mount = root.findViewById(R.id.spinner)
        year = root.findViewById(R.id.spinner2)
        money = root.findViewById(R.id.txtmoneydash)
        txtPrinciple = root.findViewById(R.id.txtmoneydashout)
        txtMoneyGet = root.findViewById(R.id.txtmoneyGet)
        monthlySale = root.findViewById(R.id.monthlySale)


        back = root.findViewById(R.id.imageviewback)
        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        spineryear.add("ทั้งหมด")
        spinermount.add(dataMonth("ทั้งหมด","ทั้งหมด"))
        monthText = ""
        yearText = ""

        showYM()
        var spineryear2 = spineryear.distinct()
        val adapterband = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, spineryear2
        )
        year?.adapter = adapterband
        year?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                yearText = parent!!.getItemAtPosition(position).toString()
                Log.d("testt", yearText.toString())
                if (yearText.toString() == "ทั้งหมด") {
                    yearText = ""
                }
                showSum()
                showMonthlySale(monthlySale!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        var spineryear3 = spinermount.distinctBy { it.monthNum }
        val adapterband2 = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, spineryear3
        )
        mount?.adapter = adapterband2
        mount?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
               val month =mount!!.selectedItem as dataMonth
                monthText=month.monthNum
                if (monthText.toString() == "ทั้งหมด") {
                    monthText = ""
                }
                showSum()


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        showMonthlySale(monthlySale!!)
        showSum()
        return root
    }



    private fun showYM() {
        var mountarray=arrayOf("","มกราคม","กุมภาพันธ์","มีนาคม","เมษายน","พฤษภาคม","มิถุนายน","กรกฎาคม","สิงหาคม","กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม")
        val url: String =
            getString(R.string.root_url) + getString(R.string.loaner_DashboradYM_url) + loanerID

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
                            spineryear.add(item.getString("years"))
                            //spinermount.add(item.getString("month"))
                            spinermount.add(dataMonth(item.getString("month"),mountarray[item.getString("month").toInt()]))


                        }

                    } else {


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

    @SuppressLint("SetTextI18n")
    private fun showSum() {
        var mouney = 0f
        var Principle = 0f
        val url: String =
            getString(R.string.root_url) + getString(R.string.loaner_DashboradSum_url) + loanerID + "?year=" + yearText + "&mount=" + monthText

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
                            mouney += (item.getString("total")).toFloat()
                            Principle += (item.getString("Principle")).toFloat()
                        }
                        money?.text = "฿$mouney"
                        txtPrinciple?.text = "฿$Principle"
                        txtMoneyGet?.text = "฿" + (mouney - Principle)
                    } else {


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

    class dataMonth(var monthNum: String,var monthText: String) {
        override fun toString(): String {
            return monthText
        }
    }

    private fun showMonthlySale(chart: BarChart) {
        val dataSets = ArrayList<IBarDataSet>()
        val entries = ArrayList<BarEntry>()
        val labels = arrayListOf<String>()
        val months = arrayOf(
            "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
            "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
        )
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_DashboradSumDetail_url)
        url += "$loanerID?year=$yearText"
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            var index = java.lang.Float.valueOf(i.toString())
                            var value = java.lang.Float.valueOf(
                                item.getString("total")
                            )
                            entries.add(BarEntry(index, value))
                            labels.add(months[item.getString("mouth").toInt() - 1])
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
                        17
                        val dataset = BarDataSet(entries, "")
                        dataset.valueTextSize = 12f
                        dataset.setColors(*ColorTemplate.COLORFUL_COLORS) // set the color
                        dataset.valueFormatter = MyValueFormatter("###,###,###,##0.0", "")
                        dataSets.add(dataset) //Data set to data
                        val data = BarData(dataSets)
                        chart.data = data
                //chart.getXAxis().setLabelRotationAngle(0);
                        chart.description.isEnabled = false //ซ่อนคา วา่ "Description Label"
                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                //Hide grid line
                        chart.xAxis.setDrawGridLines(false)
                        val xAxis = chart.xAxis
                        xAxis.labelCount = labels!!.count()
                        xAxis.textSize = 12f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
                        val LeftAxis = chart.axisLeft
                        LeftAxis.textSize = 12f
                        val RightAxis = chart.axisRight
                        RightAxis.textSize = 12f
                        RightAxis.isEnabled = false //กา หนดให้ตวัเลขดา้นขวาไม่ตอ้งแสดง
                //Define legend
                        val legend = chart.legend
                        legend.isEnabled = false


    }
}

class MyValueFormatter(pattern: String?, suffix: String) :
    ValueFormatter() {
    private val mFormat: DecimalFormat
    private val suffix: String
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble()) + suffix
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return if (axis is XAxis) {
            mFormat.format(value.toDouble())
        } else if (value > 0) {
            mFormat.format(value.toDouble()) + suffix
        } else {
            mFormat.format(value.toDouble())
        }
    }

    init {
        mFormat = DecimalFormat(pattern)
        this.suffix = suffix

    }
}