package com.example.aloan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoanerCriterionFragment : Fragment() {

    var radioagex1:RadioButton?=null
    var radioagex2:RadioButton?=null
    var radioagex3:RadioButton?=null
    var radioagex4:RadioButton?=null

    var radiomarrix1:RadioButton?=null
    var radiomarrix2:RadioButton?=null

    var radiosaralyx1:RadioButton?=null
    var radiosaralyx2:RadioButton?=null
    var radiosaralyx3:RadioButton?=null
    var radiosaralyx4:RadioButton?=null
    var radiosaralyx5:RadioButton?=null
    var radiosaralyx6:RadioButton?=null

    var editmoneyadd:EditText?=null
    var btncancel:Button?=null
    var btnadd:Button?=null
    var txtname:TextView?=null

    var ageRange=""
    var marri=""
    var saraly=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val root =   inflater.inflate(R.layout.fragment_loaner_criterion, container, false)
        radioagex1 = root.findViewById(R.id.radioagex1)
        radioagex2 = root.findViewById(R.id.radioagex2)
        radioagex3 = root.findViewById(R.id.radioagex3)
        radioagex4 = root.findViewById(R.id.radioagex4)
        radiomarrix1 = root.findViewById(R.id.radiomarrix1)
        radiomarrix2 = root.findViewById(R.id.radiomarrix2)
        radiosaralyx1 = root.findViewById(R.id.radiosaralyx1)
        radiosaralyx2 = root.findViewById(R.id.radiosaralyx2)
        radiosaralyx3 = root.findViewById(R.id.radiosaralyx3)
        radiosaralyx4 = root.findViewById(R.id.radiosaralyx4)
        radiosaralyx5 = root.findViewById(R.id.radiosaralyx5)
        radiosaralyx6 = root.findViewById(R.id.radiosaralyx6)
        editmoneyadd=root.findViewById(R.id.editmoneyadd)
        btnadd=root.findViewById(R.id.btnaddb)
        btncancel=root.findViewById(R.id.btncancel3)
        txtname=root.findViewById(R.id.txtname)

        val bundle = this.arguments

        btncancel?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,LoanerHomeFragment())
            fragmentTransaction.commit()
        }

        btnadd?.setOnClickListener {

            cri()
        addCriterion(bundle?.get("borrowlistID").toString())
        }
        txtname?.text=bundle?.get("txtname").toString()

        return root

    }

    private fun cri(){

        if(radioagex1!!.isChecked){ ageRange="0" }
        if(radioagex2!!.isChecked){ ageRange="1" }
        if(radioagex3!!.isChecked){ ageRange="2" }
        if(radioagex4!!.isChecked){ ageRange="3" }

        if(radiomarrix1!!.isChecked){ marri="0" }
        if(radiomarrix2!!.isChecked){ marri="1" }

        if(radiosaralyx1!!.isChecked){ saraly="0" }
        if(radiosaralyx2!!.isChecked){ saraly="1" }
        if(radiosaralyx3!!.isChecked){ saraly="2" }
        if(radiosaralyx4!!.isChecked){ saraly="3" }
        if(radiosaralyx5!!.isChecked){ saraly="4" }
        if(radiosaralyx6!!.isChecked){ saraly="5" }
    }

    private fun addCriterion(borrowlist:String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_AddCriterion_url)+borrowlist
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("Age_range", ageRange.toString())
                .add("Saraly_range", saraly.toString())
                .add("Married", marri.toString())
                .add("money_max", editmoneyadd?.text.toString())
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
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment,LoanerHomeFragment())
                        fragmentTransaction.commit()
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