package com.oguncan.shareapp.Fragments

import android.content.Context
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
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Activity.Constant
import com.oguncan.shareapp.Models.Post
import com.oguncan.shareapp.Models.User
import com.oguncan.shareapp.R
import com.oguncan.shareapp.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.fragment_post_next.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class PostNextFragment : Fragment() {
    lateinit var image : Uri
    lateinit var mImage : String
    var bitmap : Bitmap? = null
    var preferences : SharedPreferences? = null

    companion object{
        var GALLERY_CHANGE_POST = 3
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_post_next, container, false)
        init()
        if(image != null){
            view.imgNewShareShareImage.setImageURI(image)

        }
        view.imgNewShareBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
        view.txtNewShareOkeyButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!(view.editTextNewShareComment.text.toString().isEmpty())){
                    post(view)
                }
                else{
                    Toast.makeText(activity!!, "Açıklama girilmesi zorunludur", Toast.LENGTH_SHORT)
                }
            }

        })
        return view
    }
    fun post(view : View){
        var builder = Uri.parse(Constant.CREATE_POSTS).buildUpon()

        builder.appendQueryParameter("desc", view.editTextNewShareComment.text.toString().trim())
        builder.appendQueryParameter("photo", "file:/"+bitmapToString(bitmap))

        var registerUrl=builder.build().toString();


        var stringRequest = object : StringRequest(Request.Method.POST, registerUrl, Response.Listener { response ->
            try{
                var request = JSONObject(response)
                if(request.getBoolean("success")) {
                    var postObject = request.getJSONObject("post");
                    var userObject = postObject.getJSONObject("user")
                    var user : User = User(userObject.getInt("id"),
                    userObject.getString("username"), userObject.getString("photo"))

                    var post : Post = Post(
                        postObject.getInt("id"),
                        //postObject.getInt("score"),
                        "0",
                        "0",
                        postObject.getString("created_at"),
                        postObject.getString("desc"),
                        postObject.getString("photo"),
                        user
                    )
                    HomeFragment.arrayList!!.add(0,post)
                    HomeFragment.recyclerView!!.adapter!!.notifyItemInserted(0)
                    HomeFragment.recyclerView!!.adapter!!.notifyDataSetChanged()
                    Toast.makeText(activity!!, "Posted", Toast.LENGTH_SHORT)
                    activity!!.finish()
                }
            }catch (e : Exception){
                Log.i("This is the error2", "Error :$e")
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
                map.put("desc",  view.editTextNewShareComment.text.toString().trim())
                map.put("photo",  "file:/"+bitmapToString(bitmap)!!)
                return map
            }

            override fun getHeaders(): Map<String, String> {
                var token = preferences!!.getString("token","")
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer "+token)
                return map
            }
        }
        var queue : RequestQueue = Volley.newRequestQueue(context)
        queue.add(stringRequest)
    }
    fun init(){
        preferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)

        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, image)
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }
    @Subscribe(sticky = true)
    internal fun onEmailEvent(comingImage : EventBusDataEvents.SendShareImage){
        image = comingImage!!.image!!
        bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, image)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
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

}
