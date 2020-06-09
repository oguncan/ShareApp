package com.oguncan.shareapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.oguncan.shareapp.HomeActivity
import com.oguncan.shareapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var handler = Handler();
        handler.postDelayed(
            object : Runnable{
                override fun run() {
                    var sharedPreferences = applicationContext.getSharedPreferences("user",Context.MODE_PRIVATE)
                    var isLoggedIn : Boolean = sharedPreferences.getBoolean("isLoggedIn",false);
                    if(isLoggedIn){
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()
                    }
                    isFirstTime();
                }

            }, 1500
        )

    }
    private fun isFirstTime(){
        var sharedPreferences : SharedPreferences = application.getSharedPreferences("onBoard", Context.MODE_PRIVATE)
        var isFirstTime = sharedPreferences.getBoolean("isFirstTime",true)
        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        finish()
    }
}
