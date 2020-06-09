package com.oguncan.shareapp.Adapters

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oguncan.shareapp.Activity.Constant
import com.oguncan.shareapp.Models.Post
import com.oguncan.shareapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.ArrayList


class PostAdapter(var context : Context, var arrayList : ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostsHolder>() {

    private val searchList: ArrayList<Post>
    var preferences : SharedPreferences? = null

    init {
        preferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        searchList = ArrayList(arrayList)
    }
    class PostsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName : TextView = itemView.findViewById(R.id.txtViewHomeUsername)
        var txtDate : TextView = itemView.findViewById(R.id.txtViewHomeDate)
        var imgAccountImage : CircleImageView = itemView.findViewById(R.id.imgViewHomeAccountImg)
        var txtDesciption : TextView = itemView.findViewById(R.id.txtViewHomeDescription)
        var imgPostImage : ImageView = itemView.findViewById(R.id.imgViewHomeImage)
        var txtRating : TextView = itemView.findViewById(R.id.txtViewHomeRatingText)
        var ratingBar : RatingBar = itemView.findViewById(R.id.ratingBarHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_home, parent, false)
        return PostsHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    var filters: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults? {
            var filteredList = ArrayList<Post>()
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(searchList)
            }
            else{
                for (post in searchList) {
                    if(post.desc.toLowerCase().contains(charSequence.toString().toLowerCase())
                        || post.user.username.toLowerCase().contains(charSequence.toString().toLowerCase())){

                        filteredList.add(post)
                    }
                }
            }
            var filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(
            charSequence: CharSequence,
            filterResults: FilterResults
        ) {
            arrayList.clear()
            arrayList.addAll(filterResults.values as Collection<Post>)
            notifyDataSetChanged()
        }
    }

    fun getFilter() : Filter{
        return filters
    }



    override fun onBindViewHolder(holder: PostsHolder, position: Int) {
        var post = arrayList.get(position)
        Picasso.get().load(Constant.URL+"storage/profiles/"+post.photo).into(holder.imgAccountImage)
        Picasso.get().load(Constant.URL+"storage/posts/"+post.photo).into(holder.imgPostImage)
        holder.txtName.setText(post.user.username.toString())
        holder.txtDate.setText(post.date.toString())
        holder.txtDesciption.setText(post.desc.toString())
        holder.ratingBar.rating = 0f
        if(post.score.toFloat()==0f){
            holder.txtRating.setText("Puan ortalaması: "+0)
            holder.ratingBar.rating = 0f
        }
        else{
            holder.txtRating.setText("Puan ortalaması: "+post.score.toFloat()/post.scoreCount.toFloat())
            holder.ratingBar.rating = post.score.toFloat()/post.scoreCount.toFloat()
        }
        holder.ratingBar.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener{
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                var builder = Uri.parse(Constant.CREATE_SCORE).buildUpon()
                builder.appendQueryParameter("post_id",post.id.toString())
                builder.appendQueryParameter("score", p1.toString())
                var registerUrl=builder.build().toString();

                var stringRequest = object : StringRequest(Request.Method.POST, registerUrl, Response.Listener { response ->
                    var mPost = arrayList.get(position)
                    try{
                        var request = JSONObject(response)
                        if(request.getBoolean("success")) {
                            arrayList[position] = mPost
                            notifyItemChanged(position)
                            notifyDataSetChanged()
                        }
                        else{
                            Toast.makeText(context, "Puanlandırmayı yaptınız!", Toast.LENGTH_SHORT).show()
                            holder.ratingBar.rating = post.score.toFloat()/post.scoreCount.toFloat()
                            holder.ratingBar.isClickable=false
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
                        map.put("post_id", post.id.toString())
                        map.put("score", p1.toInt().toString())
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

        }

    }



}