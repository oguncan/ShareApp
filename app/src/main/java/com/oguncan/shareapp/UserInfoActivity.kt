package com.oguncan.shareapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Activity.Constant
import kotlinx.android.synthetic.main.activity_user_info.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URI

class UserInfoActivity : AppCompatActivity() {
    private var bitmap : Bitmap? = null
    private var sharedPreferences : SharedPreferences? = null
    var photoUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        init()
    }
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }
    fun init(){
        sharedPreferences = application.getSharedPreferences("user", Context.MODE_PRIVATE)
        var username = sharedPreferences!!.getString("username","")
        var email = sharedPreferences!!.getString("email","")
        txtViewEditProfileUsername.text = username.toString()
        txtViewEditProfileEmail.text = email.toString()
        txtUserInfoSelectPhoto.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else{
                    //system OS is < Marshmallow
                    pickImageFromGallery();
                }
            }


        })

        btnUserInfoNextButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                saveUserInfo()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            photoUri = data!!.data
            imgUserInfoProfilePhoto.setImageURI(photoUri)
            Log.i("This is the error", data.dataString)
            var pathtoimage= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath+ "/"+photoUri
            Log.i("This is the error", pathtoimage)

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)

            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
    public fun getResizedBitmap(image : Bitmap, maxSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        var bitmapRatio = width as Float / height as Float
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio) as Int
        } else {
            height = maxSize;
            width = (height * bitmapRatio) as Int
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun saveUserInfo(){
        var builder = Uri.parse(Constant.SAVE_USER_INFO).buildUpon()
        builder.appendQueryParameter("name", edtTextUserInfoName.text.toString())
        builder.appendQueryParameter("photo", bitmapToString(bitmap))
        var registerUrl=builder.build().toString();
        println(registerUrl.toString())
        Log.i("This is the error", registerUrl)
        Toast.makeText(this@UserInfoActivity, registerUrl.toString(), Toast.LENGTH_SHORT).show()
//        var imageRequest = ImageRequest(registerUrl, )
        var stringRequest = object : StringRequest(Request.Method.POST, registerUrl, Response.Listener { response ->
            try{
                var request = JSONObject(response)
                if(request.getBoolean("success")) {
                    var editor : SharedPreferences.Editor = sharedPreferences!!.edit()
                    editor.putString("token", sharedPreferences!!.getString("token",""))
                    editor.putString("photo", request.getString("photo"))
                    editor.apply()
                    startActivity(Intent(this@UserInfoActivity, HomeActivity::class.java))
                    finish()

                }
            }catch (e : java.lang.Exception){
                Log.i("This is the error", "Error :$e")
            }
        }, Response.ErrorListener { error ->
            Log.i("This is the error", "Error :$error")

            Toast.makeText(this@UserInfoActivity, "İnternet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show()
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var token = sharedPreferences!!.getString("token","")
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer "+token)
                return map
            }

            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map.put("photo", bitmapToString(bitmap)!! )
                map.put("name",edtTextUserInfoName.text.toString())
                return map
            }
        }
        var queue : RequestQueue = Volley.newRequestQueue(this@UserInfoActivity!!)
        queue.add(stringRequest)
    }


    private fun bitmapToString(bitmap: Bitmap?): String? {
        if(bitmap!=null){
            var stream : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var array = stream.toByteArray()
            return Base64.encodeToString(array, Base64.DEFAULT)
        }
        return  ""
    }
}
