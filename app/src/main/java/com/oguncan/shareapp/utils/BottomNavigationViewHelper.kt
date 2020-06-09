package com.oguncan.shareapp.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oguncan.shareapp.Activity.AddPostActivity
import com.oguncan.shareapp.Activity.EditProfileActivity
import com.oguncan.shareapp.HomeActivity
import com.oguncan.shareapp.R

class BottomNavigationViewHelper {
    companion object{
        fun setupBottomNavigationView(bottomNavigationView : BottomNavigationView){

//            bottomNavigationView.enableAnimation(false)
//            bottomNavigationView.enableShiftingMode(true)
//            bottomNavigationView.enableItemShiftingMode(true)
//            bottomNavigationView.setTextVisibility(false)
        }
        fun setupNavigation(context: Context, bottomNavigationView: BottomNavigationView){
            bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when(item.itemId){
                        R.id.navBarHomePage -> {
                            val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            context.startActivity(intent)
                            return true
                        }
                        R.id.navBarAddPostPage -> {
                            val intent = Intent(context, AddPostActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            context.startActivity(intent)
                            return true
                        }
                        R.id.navBarProfilePage -> {
                            val intent = Intent(context, EditProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            context.startActivity(intent)
                            return true
                        }
                    }
                    return false
                }

            })
            }
        }
    }