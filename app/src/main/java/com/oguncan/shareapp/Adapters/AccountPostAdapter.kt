package com.oguncan.shareapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.oguncan.shareapp.Models.Post
import com.oguncan.shareapp.R
import com.squareup.picasso.Picasso
import java.util.ArrayList

class AccountPostAdapter(
    private val context: Context,
    private val arrayList: ArrayList<Post>
) : RecyclerView.Adapter<AccountPostAdapter.AccountPostHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountPostHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_recyclerview_photo, parent, false)
        return AccountPostHolder(v)
    }

    override fun onBindViewHolder(holder: AccountPostHolder, position: Int) {
        Picasso.get().load(arrayList[position].photo).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class AccountPostHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.imgAccountPost)
        }
    }

}