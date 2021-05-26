package com.example.aloan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class LoanerEditProfileFragment : Fragment() {

    var editemail:EditText?=null
    var editphone:EditText?=null
    var radiosingle:RadioButton?=null
    var radiomarri:RadioButton?=null
    var editjob:EditText?=null
    var editsalary:EditText?=null
    var imgpro:ImageView?=null
    var editLineID:EditText?=null
    var editadress:EditText?=null
    var back:ImageView?=null
    var btnconfirm: Button?=null
    var file: File? = null
    var imageFilePath: String? = null
    var loanerID: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_edit_profile, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginLoanerActivity().appPreference, Context.MODE_PRIVATE)
        loanerID = sharedPrefer?.getString(LoginLoanerActivity().LoanerIdPreference, null)

        editemail=root.findViewById(R.id.editBank)
        editphone=root.findViewById(R.id.editphone4)
        radiomarri=root.findViewById(R.id.radiomarri)
        radiosingle=root.findViewById(R.id.radiosingle)
        editjob=root.findViewById(R.id.editjob)
        imgpro=root.findViewById(R.id.imgpro)
        editLineID=root.findViewById(R.id.editLineID1)
        back=root.findViewById(R.id.imageviewback)
        editsalary=root.findViewById(R.id.editsalary)
        editadress=root.findViewById(R.id.editadress)
        btnconfirm=root.findViewById(R.id.btnconfrim1)

        btnconfirm?.setOnClickListener {
            updateUser()
        }
        imgpro?.setOnClickListener {
            val builder1 = AlertDialog.Builder(requireActivity())
            builder1.setMessage("ท่านต้องการเลือกรูปภาพที่มีอยู่แล้ว หรือ ถ่ายภาพใหม่?")
            builder1.setNegativeButton("เลือกรูปภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
                // imageViewSlip?.visibility = View.VISIBLE
            }
            builder1.setPositiveButton("ถ่ายภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var imageURI: Uri? = null
                try {
                    imageURI = FileProvider.getUriForFile(requireActivity(),
                        BuildConfig.APPLICATION_ID.toString() + ".provider",
                        createImageFile()!!)
                } catch (e: IOException) { e.printStackTrace() }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(intent, 200)
                // imageViewSlip?.visibility = View.VISIBLE
            }

            val alert11 = builder1.create()
            alert11.show()
        }
        back?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerAccountFragment())
            fragmentTransaction.commit()
        }
        permission()
        viewloaner()
        return root
    }
    private fun viewloaner(){

        var url: String = getString(R.string.root_url) + getString(R.string.Loaner_url) + loanerID
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
                        editjob?.setText(data.getString("job"))
                        editphone?.setText(data.getString("phone"))
                        editemail?.setText(data.getString("email"))
                        editemail?.setSelection(0)
                        editsalary?.setText(data.getString("salary"))
                        editadress?.setText(data.getString("address"))
                        if(data.getString("married")=="1"){
                            radiomarri?.isChecked=true
                        }else{
                            radiosingle?.isChecked=true
                        }
                        editLineID?.setText(data.getString("LineID"))
                        var url = getString(R.string.root_url) +
                                getString(R.string.profileBLoaner_image_url) + data.getString("imageProfile")
                        Picasso.get().load(url).into(imgpro)

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
    private fun updateUser()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.LoanerUpdate_url) + loanerID
        val okHttpClient = OkHttpClient()
        var request: Request
        if(file==null){
            val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("typeUpdate", "profile")
                .addFormDataPart("Job", editjob?.text.toString())
                .addFormDataPart("Phone", editphone?.text.toString())
                .addFormDataPart("email", editemail?.text.toString())
                .addFormDataPart("Married", if (radiomarri!!.isChecked) "1" else "0")
                .addFormDataPart("Salary", editsalary?.text.toString())
                .addFormDataPart("Adress", editadress?.text.toString())
                .addFormDataPart("LineID", editLineID?.text.toString())
                .build()
            request= Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        }
        else{
            val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("typeUpdate", "profile")
                .addFormDataPart("Job", editjob?.text.toString())
                .addFormDataPart("Phone", editphone?.text.toString())
                .addFormDataPart("email", editemail?.text.toString())
                .addFormDataPart("Married", if (radiomarri!!.isChecked) "1" else "0")
                .addFormDataPart("Salary", editsalary?.text.toString())
                .addFormDataPart("Adress", editadress?.text.toString())
                .addFormDataPart("LineID", editLineID?.text.toString())

                .addFormDataPart("imageProfile", ( file?.name),
                    RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file!!))
                .build()
            request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        }

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(context, "สำเร็จ", Toast.LENGTH_LONG).show()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment,LoanerAccountFragment())
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
                imgpro?.setImageBitmap(bitmap)
                imgpro?.setImageURI(uri)
                imgpro?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse("file:$imageFilePath")
            file = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(file)
                var imageBitmap = BitmapFactory.decodeStream(ims)


                //show image
                imgpro?.setImageBitmap(imageBitmap)
                imgpro?.visibility = View.VISIBLE
                getFileName(imageUri)

            } catch (e: FileNotFoundException) {
                return
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "")
        val image = File.createTempFile(
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()), ".png",
            storageDir)
        imageFilePath = image.absolutePath
        return image
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

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(
                uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }



}