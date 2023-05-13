package com.dragosoft.gymapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityLoginBinding
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.databinding.FragmentProfileBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth

    override fun onStart(){
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) run {
            MainActivity().user = currentUser
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        profileBinding = FragmentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setPermissions("email", "public_profile",
            "user_gender", "user_birthday", "user_friends")
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
//                val graphRequest = GraphRequest.newMeRequest(result.accessToken){
//                    `object`, response -> getFacebookData(`object`)
//                }
//                val parameters = Bundle()
//                parameters.putString("fields", "id,email,birthday,friends,gender,name")
//                graphRequest.parameters = parameters
//                graphRequest.executeAsync()

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    MainActivity().user = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

//    private fun getFacebookData(obj: JSONObject?) {
//        val profilePicture = "https://graph.facebook.com/${obj?.getString("id")}/picture?width=200&height=200"
//
//        Glide.with(this)
//            .load(profilePicture)
//            .into(profileBinding.profilePicture)
//        val name = obj?.getString("name")
//        val birthday = obj?.getString("birthday")
//        val gender = obj?.getString("gender")
//        val totalCount = obj?.getJSONObject("friends")
//            ?.getJSONObject("summary")
//            ?.getString("total_count")
//
//        val email = obj?.getString("email")
//
//        profileBinding.informations.text = "Name: ${name}\n" +
//                "Email: ${email}\n+" +
//                "Gender: ${gender}\n" +
//                "Birthday: ${birthday}\n" +
//                "Number of Friends: ${totalCount}"
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}