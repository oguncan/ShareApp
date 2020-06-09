package com.oguncan.shareapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.oguncan.shareapp.Adapters.SharePagerAdapter
import com.oguncan.shareapp.Fragments.AddPostFragment
import com.oguncan.shareapp.R
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        setupViewPagerItems()
    }

    private fun setupViewPagerItems() {

        var tabLayoutItemNames = ArrayList<String>()
        tabLayoutItemNames.add("GALERİ")

        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager, tabLayoutItemNames)
        sharePagerAdapter.fragmentAdd(AddPostFragment())
        shareViewPager.adapter = sharePagerAdapter
        shareTabLayout.setupWithViewPager(shareViewPager)


    }


    override fun onBackPressed() {
        shareLayoutRoot.visibility = View.VISIBLE
        shareLayoutContainer.visibility = View.GONE
        super.onBackPressed()
    }
}
