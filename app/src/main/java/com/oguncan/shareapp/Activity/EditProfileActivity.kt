package com.oguncan.shareapp.Activity

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Adapters.AccountPostAdapter
import com.oguncan.shareapp.Fragments.AccountFragment
import com.oguncan.shareapp.Models.Post
import com.oguncan.shareapp.Models.User
import com.oguncan.shareapp.R
import com.oguncan.shareapp.utils.BottomNavigationViewHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*

import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class EditProfileActivity : AppCompatActivity() {
    var preferences : SharedPreferences? = null
    var recyclerView: RecyclerView? = null
    var adapter: AccountPostAdapter? = null
    var imgUrl : String?  = null;
    companion object{
        private val ACTIVITY_NO = 2
        private val TAG = "Profile Activity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setupNavigation()
        preferences = this@EditProfileActivity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        getData()
        editProfileRecyclerView.setHasFixedSize(true);
        editProfileRecyclerView.setLayoutManager(GridLayoutManager(applicationContext,2));
        btnEditProfileButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                editProfileRoot.visibility = View.GONE
                var transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.editProfileContainer, AccountFragment())
                transaction.addToBackStack("editProfileFragment")
                transaction.commit()
            }
        })
    }

    fun setupNavigation(){
        BottomNavigationViewHelper.setupBottomNavigationView(profileBottomNavigation)
        BottomNavigationViewHelper.setupNavigation(this,profileBottomNavigation)
        var menu = profileBottomNavigation.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onBackPressed() {
        editProfileRoot.visibility = View.VISIBLE
        super.onBackPressed()
    }

    fun getData(){
        var arrayList = ArrayList<Post>()
        var builder = Uri.parse(Constant.MY_POST).buildUpon()
//        builder.appendQueryParameter("name", view.edtTextEditProfileName.text.toString())

        var registerUrl=builder.build().toString();
        Log.i("This is the error :", registerUrl)
        val request: StringRequest =
            object : StringRequest(
                Method.GET, registerUrl,
                Response.Listener { res: String? ->
                    try {
                        val jsonObject = JSONObject(res)
                        if (jsonObject.getBoolean("success")) {
                            var posts = jsonObject.getJSONArray("posts");
                            for (i in 0 until posts.length()) {
                                var postObject = posts.getJSONObject (i);
                                var userObject = jsonObject.getJSONObject("user")
                                var user = User(userObject.getInt("id"),
                                    userObject.getString("username"), userObject.getString("photo"))
                                var post = Post(
                                    postObject.getInt("id"),
                                    postObject.getString("score"),
                                    postObject.getString("scoreCount"),
                                    postObject.getString("created_at"),
                                    postObject.getString("desc"),
                                    postObject.getString("photo"),
                                    user
                                )
                                post.photo = (Constant.URL + "storage/posts/" + postObject.getString("photo"));
                                arrayList.add(post);
                            }
                            var user = jsonObject.getJSONObject("user")
                            Log.i("This is the error :", user.getString("username"))
                            txtViewProfileUserName.setText(user.getString("username"))
                            txtViewPostCount.setText(arrayList.size.toString()+"")
                            Picasso.get().load(Constant.URL+"storage/profiles/"+user.getString("photo")).into(imgEditProfilePhoto);
                            adapter = AccountPostAdapter(this@EditProfileActivity, arrayList);
                            editProfileRecyclerView!!.adapter = adapter;
                            imgUrl = Constant.URL+"storage/profiles/"+user.getString("photo");

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

//                override fun getParams(): Map<String, String> {
//                    val map = HashMap<String, String>()
//                    map.put("photo",bitmapToString(bitmap)!! )
//                    map.put("name", view.edtTextEditProfileName.text.toString())
//                    map.put("email", view.edtTextProfileEditEmail.text.toString())
//                    map.put("username", view.edtTextProfileEditUsername.text.toString())
//                    return map
//                }
            }

        val queue = Volley.newRequestQueue(this@EditProfileActivity)
        queue.add(request)
    }
}
