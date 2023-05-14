package com.dragosoft.gymapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityLoginBinding
import com.example.gymapp.databinding.ActivityRegisterBinding
import com.facebook.appevents.ml.Utils
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    private var databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance("https://gymapp-386117-default-rtdb.europe-west1.firebasedatabase.app").reference

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullName = binding.fullname
        val email = binding.email
        val phone = binding.phone
        val password = binding.password
        val conPassword = binding.conPassword
        val register = binding.registerBtn
        val loginNowBtn = binding.loginNowBtn

        register.setOnClickListener {
            val fullNameTxt = fullName.text.toString()
            val phoneTxt = phone.text.toString()
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            val conPasswordTxt = conPassword.text.toString()

            val hashedEmail = HashUtils.sha256(emailTxt)


            if (emailTxt.isEmpty() || passwordTxt.isEmpty() || fullNameTxt.isEmpty() || phoneTxt.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            else if(!passwordTxt.equals(conPasswordTxt)){
                Toast.makeText(this, "Passwords are not matching", Toast.LENGTH_SHORT).show()
            }
            else{
                databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(hashedEmail)){
                            Toast.makeText(this@RegisterActivity, "Email is already registered", Toast.LENGTH_SHORT).show()
                        }else{
                            //phone is the unique id of every user
                            databaseReference.child("users").child(hashedEmail)
                                .child("fullname").setValue(fullNameTxt)
                            databaseReference.child("users").child(hashedEmail)
                                .child("mobile").setValue(phoneTxt)
                            databaseReference.child("users").child(hashedEmail)
                                .child("email").setValue(emailTxt)
                            databaseReference.child("users").child(hashedEmail)
                                .child("password").setValue(passwordTxt)

                            Toast.makeText(this@RegisterActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


            }

        }


        loginNowBtn.setOnClickListener{
            finish()
        }
    }


}