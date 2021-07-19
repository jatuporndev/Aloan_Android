package com.example.aloan

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.gcacace.signaturepad.views.SignaturePad
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class  RegisterLoanerActivity : AppCompatActivity() {
    var back: TextView?=null
    var editTextBirthDate: EditText?=null
    var btncalender: Button?=null
    var btnconfrim: Button?=null
    var btnclear: Button?=null
    var txtread:TextView?=null
    var check: CheckBox?=null

    var editemail:EditText?=null
    var editpass:EditText?=null
    var editconpass:EditText?=null
    var editname:EditText?=null
    var editlastname:EditText?=null
    var editphone:EditText?=null
    var editjob:EditText?=null
    var editadress:EditText?=null
    var editbank:Spinner?=null
    var editidbank:EditText?=null
    var editidcard:EditText?=null
    var editidcardback:EditText?=null
    var radioman:RadioButton?=null
    var radioWomen:RadioButton?=null
    var radiosingle:RadioButton?=null
    var radioMarried:RadioButton?=null
    var imgcard:ImageView?=null
    var imgface:ImageView?=null
    var btnimgcard: Button?=null
    var btnimgface: Button?=null
    var imgpro:ImageView?=null

    var filecard: File? = null
    var fileface: File? = null
    var filesignature: File? = null
    var imageFilePath: String? = null

    var  signaturePad: SignaturePad?=null
    var banklistID=""
    var bankname=""
    var bankimg=""
    private var bank = java.util.ArrayList<Bank>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_loaner)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        back = findViewById(R.id.back)

        editTextBirthDate=findViewById(R.id.editTextBirthDate)
        editTextBirthDate?.isEnabled=false
        btncalender=findViewById(R.id.btncalender)
        txtread=findViewById(R.id.txtread)
        check=findViewById(R.id.checkBox)
        btnconfrim=findViewById(R.id.btnconfrim)
        btnconfrim?.isEnabled =false

        editemail=findViewById(R.id.editBank)
        editpass=findViewById(R.id.editpass)
        editconpass=findViewById(R.id.editconpass)
        editname=findViewById(R.id.editname)
        editlastname=findViewById(R.id.editlastname)
        editphone=findViewById(R.id.editphone4)
        editjob=findViewById(R.id.editjob)
        editadress=findViewById(R.id.editadress)
        editbank=findViewById(R.id.editbank)
        editidbank=findViewById(R.id.editidbank)
        editidcard=findViewById(R.id.editidcard)
        editidcardback=findViewById(R.id.editidcardback)
        radioman=findViewById(R.id.radioagex1)
        radioWomen=findViewById(R.id.radiowomen)
        radiosingle=findViewById(R.id.radiosingle)
        radioMarried=findViewById(R.id.radiomarri)
        imgcard=findViewById(R.id.imgcard)
        imgface=findViewById(R.id.imgface)
        btnimgface=findViewById(R.id.btnimgface)
        btnimgcard=findViewById(R.id.btnimgcard)
        imgpro=findViewById(R.id.imgpro)

        check?.setOnClickListener {
            btnconfrim?.isEnabled = check!!.isChecked
        }
        back?.setOnClickListener {
            val intent = Intent(applicationContext, LoginLoanerActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnclear?.setOnClickListener {
            signaturePad?.clearView()
        }
        listbank()
        val adapterband = ArrayAdapter(
                this,android.R.layout.simple_spinner_item, bank)
        editbank?.adapter = adapterband

        editbank?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = editbank!!.selectedItem as Bank
                banklistID = band.banklistID
                bankname=band.bankname
                bankimg=band.imagebank
                var url = getString(R.string.root_url) +
                        getString(R.string.bank_image_url) + bankimg
                Picasso.get().load(url).into(imgpro)
                Log.d("text",url.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Date picker (Birth date)
        val myCalendar = Calendar.getInstance()
        val date =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val myFormat = "yyyy-MM-dd" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    editTextBirthDate?.setText(sdf.format(myCalendar.time))
                    editTextBirthDate?.setTextColor(Color.parseColor("#FF000000"))
                }
        btncalender?.setOnClickListener {
            DatePickerDialog(this, date,
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]).show()
        }

        var view: View = layoutInflater.inflate(R.layout.read,null)
        var btnreaded:Button = view.findViewById(R.id.btnreaded)
        var txtpp:TextView=view.findViewById(R.id.txtpp)

        var text:String=""
        try {
           var io: InputStream = assets.open("pp.txt")
            var size:Int = io.available()
            var buffer=ByteArray(size)
            io.read(buffer)
            io.close()
            text= String(buffer)
        }catch (ex : IOException){
            ex.printStackTrace()
        }
        txtpp.text=text

        var dialog: Dialog = Dialog(this,android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight)
        dialog.setContentView(view)
        txtread?.setOnClickListener {
            dialog.show()
        }
        btnreaded?.setOnClickListener {
            dialog.dismiss()
        }

        btnimgcard?.setOnClickListener {
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("ท่านต้องการเลือกรูปภาพที่มีอยู่แล้ว หรือ ถ่ายภาพใหม่?")
            builder1.setNegativeButton("เลือกรูปภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
                imgcard?.visibility = View.VISIBLE
            }
            builder1.setPositiveButton("ถ่ายภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var imageURI: Uri? = null
                try {
                    imageURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID.toString() + ".provider",
                        createImageFile()!!)
                } catch (e: IOException) { e.printStackTrace() }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(intent, 200)
                imgcard?.visibility = View.VISIBLE
            }

            val alert11 = builder1.create()
            alert11.show()
        }
        btnimgface?.setOnClickListener {
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("ท่านต้องการเลือกรูปภาพที่มีอยู่แล้ว หรือ ถ่ายภาพใหม่?")
            builder1.setNegativeButton("เลือกรูปภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 300)
                imgface?.visibility = View.VISIBLE
            }
            builder1.setPositiveButton("ถ่ายภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var imageURI: Uri? = null
                try {
                    imageURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID.toString() + ".provider",
                        createImageFile()!!)
                } catch (e: IOException) { e.printStackTrace() }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(intent, 400)
                imgface?.visibility = View.VISIBLE
            }

            val alert11 = builder1.create()
            alert11.show()
        }
        btnconfrim?.setOnClickListener {
            if(editpass?.text.toString()==editconpass?.text.toString()){
              // sig()
                register()
            }else{
                Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_LONG).show()
            }
        }

        permission()

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun sig(){
        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss")
        val currentDate = sdf.format(Date())+editidcard?.text.toString()
        val signature = signaturePad?.signatureBitmap;
        filesignature = bitmapToFile(signature!!, "$currentDate.png")


    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun permission()
    {
        //Set permission to open camera and access a directory
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this,
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
            filecard = File(getFilePath(uri))
            val bitmap: Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                //show image
                imgcard?.setImageBitmap(bitmap)
                imgcard?.setImageURI(uri)
                imgcard?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse("file:$imageFilePath")
            filecard = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(filecard)
                var imageBitmap = BitmapFactory.decodeStream(ims)
                imageBitmap = resizeImage(imageBitmap, 1024, 1024) //resize image
              // imageBitmap = resolveRotateImage(imageBitmap, imageFilePath!!) //Resolve auto rotate image

                //show image
                imgcard?.setImageBitmap(imageBitmap)
                imgcard?.visibility = View.VISIBLE
                getFileName(imageUri)

            } catch (e: FileNotFoundException) {
                return
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == 300 && resultCode == Activity.RESULT_OK && null != intent) {
            val uri = intent.data
            fileface = File(getFilePath(uri))
            val bitmap: Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                //show image
                imgface?.setImageBitmap(bitmap)
                imgface?.setImageURI(uri)
                imgface?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse("file:$imageFilePath")
            fileface = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(fileface)
                var imageBitmap = BitmapFactory.decodeStream(ims)
                imageBitmap = resizeImage(imageBitmap, 1024, 1024) //resize image
                 imageBitmap = resolveRotateImage(imageBitmap, imageFilePath!!) //Resolve auto rotate image

                //show image
                imgface?.setImageBitmap(imageBitmap)
                imgface?.visibility = View.VISIBLE
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
        val cursor = contentResolver.query(
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
            val cursor = contentResolver.query(
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
    private fun resizeImage(bm: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm!!.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }
    private fun resolveRotateImage(bitmap: Bitmap?, photoPath: String): Bitmap? {
        val ei = ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED)
        var rotatedBitmap: Bitmap? = null
        rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height,
            matrix, true)
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
    private fun register()
    {
        Log.d("rrr","1")
        var url: String = getString(R.string.root_url) + getString(R.string.registerLoaner_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("FirstName", editname?.text.toString())
            .addFormDataPart("LastName", editlastname?.text.toString())
            .addFormDataPart("Phone", editphone?.text.toString())
            .addFormDataPart("adress", editadress?.text.toString())

            .addFormDataPart("Brithday", editTextBirthDate?.text.toString())
            .addFormDataPart("Password", editpass?.text.toString())
            .addFormDataPart("email", editemail?.text.toString())
            .addFormDataPart("IDCard", editidcard?.text.toString())
            .addFormDataPart("IDBank", editidbank?.text.toString())
            .addFormDataPart("Bank", bankname.toString())
            .addFormDataPart("Job", editjob?.text.toString())
            .addFormDataPart("IDCard_back", editidcardback?.text.toString())

            .addFormDataPart("Gender", if (radioWomen!!.isChecked) "1" else "0")
            .addFormDataPart("Married", if (radioMarried!!.isChecked) "1" else "0")

            .addFormDataPart("filecard", (filecard?.name),
                RequestBody.create("application/octet-stream".toMediaTypeOrNull(), filecard!!))
            .addFormDataPart("fileVe", ( fileface?.name),
                RequestBody.create("application/octet-stream".toMediaTypeOrNull(), fileface!!))
           // .addFormDataPart("filesig", ( filesignature?.name),
          //     RequestBody.create("application/octet-stream".toMediaTypeOrNull(), filesignature!!))


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
                        Toast.makeText(this, "สมัครสมาชิกเรียบร้อยแล้ว รอยืนยันตัวตน", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LoginLoanerActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "อีเมลถูกใช้ไปแล้ว", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "อีเมลซ้ำ", Toast.LENGTH_LONG).show()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "อีเมลซ้ำ", Toast.LENGTH_LONG).show()
        }
    }

    class Bank(var banklistID: String, var bankname: String,var imagebank: String) {
        override fun toString(): String {
            return bankname
        }

    }
    private fun listbank() {

        val urlProvince: String = getString(R.string.root_url) + getString(R.string.loaner_allbank_url)
        Log.d("text",urlProvince)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            Log.d("text",response.isSuccessful.toString())
            if (response.isSuccessful) {
                Log.d("text","222")
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            bank.add(
                                    Bank(
                                            item.getString("banklistID"),
                                            item.getString("bankname"),
                                            item.getString("imagebank")
                                    )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else {    Log.d("text",response.code.toString() )
                response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

}