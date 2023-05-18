package com.dragosoft.gymapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityMainBinding
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseUser

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var callbackManager: CallbackManager

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val data = intent.extras
        val name = data?.getString("fullname")
        val email = data?.getString("email")
        val phone = data?.getString("phone")
        val photo = data?.getString("photo")

        val profileFragment = ProfileFragment.newInstance(name, email, phone, photo)

        callbackManager = CallbackManager.Factory.create()

        if (savedInstanceState == null){
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(profileFragment)
                R.id.locations -> replaceFragment(LocationsFragment())

                else -> {}
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun replaceFragment(fragment : Fragment){
       supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}