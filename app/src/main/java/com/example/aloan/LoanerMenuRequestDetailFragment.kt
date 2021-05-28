package com.example.aloan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class LoanerMenuRequestDetailFragment : Fragment() {

    var txtname:TextView?=null
    var txtemail:TextView?=null
    var txtphone:TextView?=null
    var txtLineId:TextView?=null
    var txtBirthday:TextView?=null
    var txtage:TextView?=null
    var txtaddress:TextView?=null

    var txtmoneyRequest:TextView?=null
    var txtinstullmentRequest:TextView?=null
    var txtInterestRequest:TextView?=null
    var txtInterest_penaltyRequest:TextView?=null
    var date:TextView?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_menu_request_detail, container, false)




        return root
    }

}