package com.example.aloan

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.*


class LoanerMenuWaitingPayDetailFragment : Fragment() {
    var back:ImageView?=null
    var imgpro:ImageView?=null
    var txtname:TextView?=null
    var txtemail:TextView?=null
    var txtphone:TextView?=null
    var txtline:TextView?=null
    var txtmoney:TextView?=null
    var txtinstu:TextView?=null
    var txtgender:TextView?=null
    var imggender:ImageView?=null


    var txtbank:TextView?=null
    var txtbankNumber:TextView?=null
    var txtnamehold:TextView?=null
    var imgbank:ImageView?=null
    var txtmoneytranfer:TextView?=null
    var imgslip:ImageView?=null
    var btnupload:Button?=null
    var btnOk:Button?=null
    var ck1:CheckBox?=null
    var ck2:CheckBox?=null
    var date:TextView?=null

    var file: File? = null
    var imageFilePath: String? = null
    var progressDialog:ProgressDialog?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_menu_waiting_pay_detail, container, false)
        val bundle=this.arguments

        back=root.findViewById(R.id.imageViewback)
        imgpro=root.findViewById(R.id.imgpro)
        txtname=root.findViewById(R.id.txtnameB)
        txtemail=root.findViewById(R.id.txtemailB)
        txtphone=root.findViewById(R.id.txtPhoneB)
        txtline=root.findViewById(R.id.txtline)
        txtmoney=root.findViewById(R.id.txtmoneyB)
        txtinstu=root.findViewById(R.id.txtinstuB)

        txtbank=root.findViewById(R.id.txtbankB)
        txtbankNumber=root.findViewById(R.id.txtbankNumberB)
        txtnamehold=root.findViewById(R.id.txtnameBB)
        imgbank=root.findViewById(R.id.imgpro2)
        txtmoneytranfer=root.findViewById(R.id.txtMoneyBB)
        imgslip=root.findViewById(R.id.imgslip)
        btnupload=root.findViewById(R.id.buttonupload)
        btnOk=root.findViewById(R.id.btnpass)
        ck1=root.findViewById(R.id.checkBox2)
        ck2=root.findViewById(R.id.checkBox3)
        txtgender=root.findViewById(R.id.txtgenderB)
        imggender=root.findViewById(R.id.imggenderB)
        date=root.findViewById(R.id.txtdayReB)

        btnOk?.isEnabled=false

        ck1?.setOnClickListener {
            btnOk?.isEnabled = ck1?.isChecked!! && ck2?.isChecked!!
        }
        ck2?.setOnClickListener {
            btnOk?.isEnabled = ck1?.isChecked!! && ck2?.isChecked!!
        }

        btnOk?.setOnClickListener {
            progressDialog = ProgressDialog(requireContext())
            progressDialog?.setTitle("กำลังดำเนินการ")
            progressDialog?.setMessage("กรุณารอซักครู่......")
            progressDialog?.show()
            Handler().postDelayed({

                addBorrowdetail(bundle?.get("RequestID").toString())
            }, 0)


        }


        btnupload?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }


        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        permission()
        viewdetailborroweer(bundle?.get("RequestID").toString())
        return root
    }
    
    private fun viewdetailborroweer(RequrstID:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_DetailMenu1request_url) + RequrstID
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
                        txtphone?.text=data.getString("phone")
                        txtemail?.text=data.getString("email")
                        txtline?.text=data.getString("LineID")
                        txtmoney?.text="฿"+data.getString("money_confirm")
                        txtinstu?.text=data.getString("instullment_confirm")

                        txtbankNumber?.text=data.getString("IDBank")
                        txtnamehold?.text="$fristname $lastname"
                        txtmoneytranfer?.text="฿"+data.getString("money_confirm")

                        date?.text=data.getString("dateAccept")

                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBorrower_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

                        if (data.getString("gender")=="0"){
                            txtgender?.text="ชาย"
                            imggender?.setImageResource(R.drawable.male)
                        }else{
                            txtgender?.text="หญิง"
                            imggender?.setImageResource(R.drawable.femenine)
                        }
                        viewbank(data.getString("bank"))

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
    private fun viewbank(bankname:String){

        var url: String = getString(R.string.root_url) + getString(R.string.loaner_viewBankfromname_url) + bankname
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

                        txtbank?.text=data.getString("bankname")
                        var url = getString(R.string.root_url) +
                                getString(R.string.bank_image_url) + data.getString("imagebank")
                        Picasso.get().load(url).into(imgbank)


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
    private fun addBorrowdetail(RequrstID: String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.loaner_addBorrowDetail_url)+RequrstID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("receipt_slip", (file?.name),
                        RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file!!))

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
                        Toast.makeText(requireContext(), "สำเร็จ", Toast.LENGTH_LONG).show()
                        progressDialog?.dismiss()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
                        fragmentTransaction.commit()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "ไม่สำเร็จ", Toast.LENGTH_LONG).show()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "ไม่สำเร็จ", Toast.LENGTH_LONG).show()
        }
    }

    private fun permission()
    {
        //Set permission to open camera and access a directory
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 225)
        }
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != intent) {
            val uri = intent.data
            file = File(getFilePath(uri))
            val bitmap: Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                //show image
                imgslip?.setImageBitmap(bitmap)
                imgslip?.setImageURI(uri)
                imgslip?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getFilePath(uri: Uri?): String? {

        var path = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        // Split at colon, use second item in the arraygetDocumentId(uri)
        val id = wholeID.split(":".toRegex()).toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)
        var columnIndex = 0
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }





}