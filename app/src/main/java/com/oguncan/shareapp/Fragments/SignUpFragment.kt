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

import com.oguncan.shareapp.R
import com.oguncan.shareapp.UserInfoActivity
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import org.json.JSONObject
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        init(view)
        return view
    }

    private fun init(view : View){
        view.txtViewSignUpLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frameAuthContainer, SignInFragment()).commit()
            }
        })

        view.btnSignUpNextButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(validate()){
                    register()
                }
            }

        })



        view.edtTextSignUpEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!view.edtTextSignUpEmail.text.toString().isEmpty()){
                    txtInputLayoutSignUpEmail.isErrorEnabled=false
                }
            }

        })

        view.edtTextSignUpUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!view.edtTextSignUpUsername.text.toString().isEmpty()){
                    txtInputLayoutSignUpUsername.isErrorEnabled=false
                }
            }

        })

        view.edtTextSignUpPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!view.edtTextSignUpPassword.text.toString().isEmpty()){
                    txtInputLayoutSignUpPassword.isErrorEnabled=false
                }
            }

        })



    }
    private fun validate() : Boolean{
        if(edtTextSignUpEmail.text.toString().isEmpty()){
            txtInputLayoutSignUpEmail.isErrorEnabled=true
            txtInputLayoutSignUpEmail.error = "Email girişi yapılmadı!"
            return false
        }
        if(edtTextSignUpPassword.text.toString().length < 8){
            txtInputLayoutSignUpPassword.isErrorEnabled=true
            txtInputLayoutSignUpPassword.error = "Şifre 8 karakterden fazla olmalıdır!"
            return false
        }
        if(edtTextSignUpUsername.text.toString().isEmpty()){
            txtInputLayoutSignUpUsername.isErrorEnabled=true
            txtInputLayoutSignUpUsername.error = "Kullanıcı adı girişi yapılmadı!"
            return false
        }
        return true
    }

    private fun register() {
        var builder = Uri.parse(Constant.REGISTER).buildUpon()
        builder.appendQueryParameter("username", edtTextSignUpUsername.text.toString().trim())
        builder.appendQueryParameter("password", edtTextSignUpPassword.text.toString().trim())
        builder.appendQueryParameter("email", edtTextSignUpEmail.text.toString().trim())
        var registerUrl=builder.build().toString();
        var stringRequest = object : StringRequest(Request.Method.POST, registerUrl, Response.Listener { response ->
            try{
                Log.i("This is the error", "Error :$response")
                var request = JSONObject(response)
                Log.e("response",response.toString())
                if(request.getBoolean("success")) {
                    var user = request.getJSONObject("user");
                    var userPref = activity!!.applicationContext.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                    )
                    var editor: SharedPreferences.Editor = userPref.edit()
                    editor.putString("token", request.getString("token"))
                    editor.putString("name", user.getString("name"))
                    editor.putString("username", user.getString("username"))
                    editor.putString("email", user.getString("email"))
                    editor.putString("photo", user.getString("photo"))
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    startActivity(Intent(activity!!, UserInfoActivity::class.java))
                    activity!!.finish()
                    //if okey
                    Toast.makeText(activity, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(activity, "Kullanıcı adı veya E-mail adresi kullanılıyor!", Toast.LENGTH_SHORT).show()
                }
            }catch (e : Exception){
                Log.i("This is the error", "Error :$e")
            }
        }, Response.ErrorListener { error ->
            Log.i("This is the error", "Error :$error")
            Toast.makeText(activity, "İnternet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show()
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map.put("username", edtTextSignUpUsername.text.toString().trim())
                map.put("password",  edtTextSignUpPassword.text.toString().trim())
                map.put("email",  edtTextSignUpEmail.text.toString().trim())
                return map
            }
        }
        var queue : RequestQueue = Volley.newRequestQueue(context)
        queue.add(stringRequest)

    }

}
