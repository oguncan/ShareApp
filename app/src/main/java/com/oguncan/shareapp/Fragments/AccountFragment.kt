package com.oguncan.shareapp.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Activity.AuthActivity
import com.oguncan.shareapp.Activity.Constant
import com.oguncan.shareapp.R
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.edtTextEditProfileName
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {
    companion object{
        val CHANGE_PHOTO = 100
    }
    var preferences : SharedPreferences? = null
    var profilePhotoURI : Uri? = null
    var bitmap : Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_account, container, false)
        init(view)

        view.txtEditProfileChangePhoto.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, CHANGE_PHOTO)
        }

        view.imgEditProfileClose.setOnClickListener {
            activity?.onBackPressed()
        }



        return view
    }

    fun init(view : View){
        preferences = getContext()!!.getSharedPreferences("user", Context.MODE_PRIVATE);
        view.btnLogOut.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                logout()
            }

        })
        view.imgEditProfileDone.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                changeProfile(view)
            }

        })
    }

    private fun changeProfile(view : View){
        var builder = Uri.parse(Constant.SAVE_USER_INFO).buildUpon()
        builder.appendQueryParameter("name", view.edtTextEditProfileName.text.toString())
        builder.appendQueryParameter("email", view.edtTextProfileEditEmail.text.toString())
        builder.appendQueryParameter("username", view.edtTextProfileEditUsername.text.toString())
        builder.appendQueryParameter("photo", bitmapToString(bitmap))
        var registerUrl=builder.build().toString();
        Log.i("This is the error :", registerUrl)
        val request: StringRequest =
            object : StringRequest(
                Method.POST, registerUrl,
                Response.Listener { res: String? ->
                    try {
                        val jsonObject = JSONObject(res)
                        if (jsonObject.getBoolean("success")) {
                            var editor : SharedPreferences.Editor = preferences!!.edit()
                            editor.putString("token", preferences!!.getString("token",""))
                            editor.putString("photo", jsonObject.getString("photo"))
                            editor.apply()
                            activity!!.finish()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError -> error.printStackTrace() }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val token = preferences!!.getString("token", "")
                    val map: HashMap<String, String> = HashMap()
                    map["Authorization"] = "Bearer $token"
                    return map
                }

                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map.put("photo",bitmapToString(bitmap)!! )
                    map.put("name", view.edtTextEditProfileName.text.toString())
                    map.put("email", view.edtTextProfileEditEmail.text.toString())
                    map.put("username", view.edtTextProfileEditUsername.text.toString())
                    return map
                }
            }

        val queue = Volley.newRequestQueue(activity!!)
        queue.add(request)
    }
    private fun bitmapToString(bitmap: Bitmap?): String? {
        if(bitmap!=null){
            var stream : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            var array = stream.toByteArray()
            return Base64.encodeToString(array, Base64.DEFAULT)

        }
        return  ""
    }

    fun logout(){
        val request: StringRequest =
            object : StringRequest(
                Method.GET, Constant.LOGOUT,
                Response.Listener { res: String? ->
                    try {
                        val jsonObject = JSONObject(res)
                        if (jsonObject.getBoolean("success")) {
                            val editor = preferences!!.edit()
                            editor.clear()
                            editor.apply()
                            startActivity(
                                Intent(
                                    this.activity,
                                    AuthActivity::class.java
                                )
                            )
                            this.activity!!.finish();

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError -> error.printStackTrace() }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val token = preferences!!.getString("token", "")
                    val map: HashMap<String, String> = HashMap()
                    map["Authorization"] = "Bearer $token"
                    return map
                }
            }

        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CHANGE_PHOTO && resultCode== AppCompatActivity.RESULT_OK && data!!.data!=null){
            profilePhotoURI = data!!.data
            view!!.imgProfilePhoto.setImageURI(profilePhotoURI)
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver, profilePhotoURI)
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }

    }

    fun getData(){
        var builder = Uri.parse(Constant.CREATE_SCORE).buildUpon()
//            builder.appendQueryParameter("post_id",post.id.toString())
//            builder.appendQueryParameter("score", p1.toString())
        var registerUrl=builder.build().toString();
        var stringRequest = object : StringRequest(Request.Method.POST, registerUrl, Response.Listener { response ->
            try{
                var request = JSONObject(response)
                if(request.getBoolean("success")) {

                }
                else{

                }
            }catch (e : Exception){

            }
        }, Response.ErrorListener { error ->
            Log.i("This is the error", "Error :$error")
            Toast.makeText(context, "Puanlandırmayı yaptınız!", Toast.LENGTH_SHORT).show()
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
//                map.put("post_id", post.id.toString())
//                map.put("score", p1.toInt().toString())
                return map
            }

            override fun getHeaders(): Map<String, String> {
                var token = preferences!!.getString("token","")
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer "+token)
                return map
            }
        }
    }


}
