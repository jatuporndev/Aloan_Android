package com.example.aloan

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class BorrowerPaymentFragment : Fragment() {
    var back:ImageView?=null
    var totalmoney:TextView?=null
    var bankNumber:TextView?=null
    var slip:ImageView?=null
    var checkBox1:CheckBox?=null
    var checkBox2:CheckBox?=null
    var btnconfirm:Button?=null
    var progressDialog:ProgressDialog?=null
    var recyclerView:RecyclerView?=null
    var btnupload:Button?=null
    var file: File? = null
    var moneyRemain:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_borrower_payment, container, false)

        val bundle=this.arguments
        var borrowdetailID =bundle?.get("borrowDetailID").toString()
        moneyRemain = bundle?.get("moneytoRemain").toString()
        back=root.findViewById(R.id.imageviewback)
        totalmoney=root.findViewById(R.id.txttotalmoney)
        bankNumber=root.findViewById(R.id.editbanknumber)
        slip=root.findViewById(R.id.imgslip)
        checkBox1=root.findViewById(R.id.checkBox5)
        checkBox2=root.findViewById(R.id.checkBox6)
        btnconfirm=root.findViewById(R.id.btnconfrim1)
        recyclerView=root.findViewById(R.id.recyclerView3)
        btnupload=root.findViewById(R.id.btnupload)

        btnconfirm?.isEnabled=false

        checkBox1?.setOnClickListener {
            btnconfirm?.isEnabled = checkBox1?.isChecked == true && checkBox2?.isChecked == true
        }
        checkBox2?.setOnClickListener {
            btnconfirm?.isEnabled = checkBox1?.isChecked == true && checkBox2?.isChecked == true
        }

        back?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("borrowDetailID", borrowdetailID)
            val fm = BorrowerMenuPayingDetailFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

       val updatehis = bundle?.getStringArrayList("arrayID")

        totalmoney?.text=  String.format("%.2f", bundle?.get("moneytotal").toString().toFloat())

        btnconfirm?.setOnClickListener {
                progressDialog = ProgressDialog(requireContext())
                progressDialog?.setTitle("กำลังดำเนินการ")
                progressDialog?.setMessage("กรุณารอซักครู่......")
                progressDialog?.show()
                Handler().postDelayed({
                    createHistoryBill(borrowdetailID,bundle?.get("moneytotal").toString())

                    if (updatehis != null) {
                        for (i in updatehis){
                            updateHistory(i.toString())

                        }
                    }
                    val bundle = Bundle()
                    bundle.putString("borrowDetailID", borrowdetailID)
                    val fm = BorrowerMenuPayingDetailFragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()
                    progressDialog?.dismiss()

                }, 100)


        }
        btnupload?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        permission()
        showbank(bundle?.get("loanerID").toString())

        return root
    }
    private fun showbank(loanerID:String) {
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
            holder.btndelete.visibility=View.INVISIBLE
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

    private fun createHistoryBill(BorrowDetailID:String,moneytotal:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.createHis_url) + BorrowDetailID+"/"+moneytotal
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("money", moneyRemain.toString())
                .addFormDataPart("imageBill", (file?.name),
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
    private fun updateHistory(historyID:String )
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updateStatusHistory_url) + historyID
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
                slip?.setImageBitmap(bitmap)
                slip?.setImageURI(uri)
                slip?.visibility = View.VISIBLE
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