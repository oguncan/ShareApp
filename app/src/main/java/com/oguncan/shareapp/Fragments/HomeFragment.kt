package com.oguncan.shareapp.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Adapters.PostAdapter
import com.oguncan.shareapp.Activity.Constant
import com.oguncan.shareapp.HomeActivity
import com.oguncan.shareapp.Models.Post
import com.oguncan.shareapp.Models.User

import com.oguncan.shareapp.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    var sharedPreferences : SharedPreferences? = null
    lateinit var postsAdapter : PostAdapter
    companion object{
        var recyclerView : RecyclerView? =  null
        var arrayList : ArrayList<Post>? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        return view
    }

    fun init(view : View){
        sharedPreferences = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        recyclerView = view.homeRecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        (context as HomeActivity).setSupportActionBar(view.toolbar)
        (context as HomeActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        getPosts(view)

        view.homeSwipeRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                getPosts(view)
            }

        })
    }

    fun  getPosts(view : View){
        arrayList = ArrayList<Post>()
        view!!.homeSwipeRefresh.isRefreshing = true
        var stringRequest = object : StringRequest(Request.Method.GET, Constant.POSTS, Response.Listener { response ->
            try{
                var request = JSONObject(response)
                if(request.getBoolean("success")) {
                    var array = JSONArray(request.getString("posts"))

                    for(value in 0 until array.length()){
                        var postObject = array.getJSONObject(value)
                        var userObject = postObject.getJSONObject("user")

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
                        arrayList!!.add(post)
                    }
                    var userPref = activity!!.applicationContext.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                    )
                    postsAdapter = PostAdapter(activity!!, arrayList!!)
                    view.homeRecyclerView.adapter = postsAdapter
                }

            }catch (e : Exception){
                Log.i("This is the error", "Error :$e")
            }
            view!!.homeSwipeRefresh.isRefreshing = false
        }, Response.ErrorListener { error ->
            Log.i("This is the error", "Error :$error")
            view!!.homeSwipeRefresh.isRefreshing = false
            Toast.makeText(activity, "İnternet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show()
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
        }
        var queue : RequestQueue = Volley.newRequestQueue(context)
        queue.add(stringRequest)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        var menuItem = menu.findItem(R.id.search)
        var searchView : SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                postsAdapter.getFilter().filter(newText)
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}
