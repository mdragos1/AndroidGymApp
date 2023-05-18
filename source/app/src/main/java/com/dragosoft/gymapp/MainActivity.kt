package com.dragosoft.gymapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityMainBinding
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.values

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var callbackManager: CallbackManager
    private var databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance("https://gymapp-386117-default-rtdb.europe-west1.firebasedatabase.app").reference

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var trainers = databaseReference.child("trainers")

        val nameList = ArrayList<String>()
        val emailList = ArrayList<String>()
        val phoneList = ArrayList<String>()
        val photoList = ArrayList<String>()


        trainers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (t in snapshot.children){
                    nameList.add(t.child("name").value as String)
                    emailList.add(t.child("email").value as String)
                    phoneList.add((t.child("phone").value as Long).toString())
                    photoList.add(t.child("photo").value as String)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })





        val data = intent.extras
        val name = data?.getString("fullname")
        val email = data?.getString("email")
        val phone = data?.getString("phone")
        val photo = data?.getString("photo")


        val profileFragment = ProfileFragment.newInstance(name, email, phone, photo)
        val trainersFragment =TrainersFragment.newInstance(nameList, emailList, phoneList, photoList)

        callbackManager = CallbackManager.Factory.create()

        if (savedInstanceState == null){
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(profileFragment)
                R.id.locations -> replaceFragment(trainersFragment)

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