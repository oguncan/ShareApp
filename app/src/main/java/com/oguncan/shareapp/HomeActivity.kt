package com.oguncan.shareapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nostra13.universalimageloader.core.ImageLoader
import com.oguncan.shareapp.Adapters.PostAdapter
import com.oguncan.shareapp.Fragments.AccountFragment
import com.oguncan.shareapp.Fragments.AddPostFragment
import com.oguncan.shareapp.Fragments.HomeFragment
import com.oguncan.shareapp.utils.BottomNavigationViewHelper
import com.oguncan.shareapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    companion object{
        var GALLERY_ADD_POST = 2
        private val ACTIVITY_NO = 0
        private val TAG = "HomeActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigation()
        var fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.homeFragmentContainer, HomeFragment()).commit()
        init()
    }

    fun init(){
        initImageLoader()
    }

    fun setupNavigation(){
        BottomNavigationViewHelper.setupBottomNavigationView(homeBottomNavigation)
        BottomNavigationViewHelper.setupNavigation(this,homeBottomNavigation)
        var menu = homeBottomNavigation.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_ADD_POST && resultCode == Activity.RESULT_OK){
            var imgUri = data!!.data
            var intent = Intent(this@HomeActivity, PostAdapter::class.java)
            intent.setData(imgUri)
            startActivity(intent)

        }
    }
    fun initImageLoader(){
        var universalImageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }
}
