package com.oguncan.shareapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oguncan.shareapp.Fragments.SignInFragment
import com.oguncan.shareapp.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportFragmentManager.beginTransaction().replace(R.id.frameAuthContainer, SignInFragment()).commit()

    }
}
