package com.oguncan.shareapp.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Activity.Constant
import com.oguncan.shareapp.HomeActivity

import com.oguncan.shareapp.R
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.edtTextSignInUsername
import org.json.JSONObject
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        init(view);
        return view
    }
    private fun init(view : View){

        view.txtViewSignInRegister.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frameAuthContainer, SignUpFragment()).commit()
            }
        })

        view.btnSignInNextButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(validate()){
                    login();
                }
            }

        })

        view.edtTextSignInUsername.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!view.edtTextSignInUsername.text.toString().isEmpty()){
                    txtInputLayoutSignInUsername.isErrorEnabled=false
                }
            }

        })

        view.edtTextSignInPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(view.edtTextSignInPassword.text.toString().length > 7){
                    txtInputLayoutSignInPassword.isErrorEnabled=false
                }
            }

        })
    }

    private fun validate() : Boolean{
        if(edtTextSignInUsername.text.toString().isEmpty()){
            txtInputLayoutSignInUsername.isErrorEnabled=true
            txtInputLayoutSignInUsername.error = "Kullanıcı adı girişi yapılmadı!"
            return false
        }
        if(edtTextSignInPassword.text.toString().length < 8){
            txtInputLayoutSignInPassword.isErrorEnabled=true
            txtInputLayoutSignInPassword.error = "Şifre 8 karakterden fazla olmalıdır!"
            return false
        }



        return true;
    }

    private fun login(){
        var builder = Uri.parse(Constant.LOGIN).buildUpon()
        builder.appendQueryParameter("username", edtTextSignInUsername.text.toString().trim())
        builder.appendQueryParameter("password", edtTextSignInPassword.text.toString().trim())
        var loginUrl=builder.build().toString();
        var stringRequest = object : StringRequest(Request.Method.POST, loginUrl, Response.Listener { response ->
            try{
                var request = JSONObject(response)
                Log.i("This is the error", "Error :${request}")
                Log.e("response",response.toString())
                if(request.getBoolean("success")) {
                    var user = request.getJSONObject("user");
                    Log.i("This is the error", "Error :${user}")
                    var userPref = activity!!.applicationContext.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                    )
                    var editor: SharedPreferences.Editor = userPref.edit()
                    editor.putString("token", request.getString("token"))
                    editor.putString("name", user.getString("name"))
                    editor.putString("username", user.getString("username"))
                    editor.putString("photo", user.getString("photo"))
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    startActivity(Intent(activity!!, HomeActivity::class.java))
                    activity!!.finish()
                    //if okey
                    Toast.makeText(context, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Kullanıcı adı ve parolanızı yanlış girdiniz!", Toast.LENGTH_SHORT).show()
                }
            }catch (e : Exception){
                Toast.makeText(context, "Kullanıcı adı ve parolanızı yanlış girdiniz!", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
//            Log.i("This is the error", "Error :$error")
            Toast.makeText(context, "Kullanıcı adı ve parolanızı yanlış girdiniz!", Toast.LENGTH_SHORT).show()
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                Log.i("This is the error", "Error :${edtTextSignInUsername.text.toString().trim()}")
                map.put("username", edtTextSignInUsername.text.toString().trim())
                map.put("password",  edtTextSignInPassword.text.toString().trim())
                Log.i("This is the error", "Error :${map["username"]}")
                return map
            }

        }

        var queue : RequestQueue = Volley.newRequestQueue(context)
        queue.add(stringRequest)
    }

}


/*
var request = StringRequest(Request.Method.POST, Constant.LOGIN, Response.Listener{
            try {
                var response = JSONObject(it)
                Log.e("response",response.toString())
                if(response.getBoolean("success")){
                    var user = response.getJSONObject("user");
                    var userPref = activity!!.applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                    var editor : SharedPreferences.Editor = userPref.edit()
                    editor.putString("token", response.getString("token"))
                    editor.putString("name", user.getString("name"))
                    editor.putString("username", user.getString("username"))
                    editor.putString("photo", user.getString("photo"))
                    editor.apply()
                    //if okey
                    Toast.makeText(activity, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                }

            }catch (e : Exception){

            }
        }, Response.ErrorListener {
            it.stackTrace

        })


        var queue : RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)
 */