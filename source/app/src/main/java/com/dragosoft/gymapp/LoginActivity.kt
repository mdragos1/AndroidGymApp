package com.dragosoft.gymapp

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityLoginBinding
import com.example.gymapp.databinding.FragmentProfileBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    public var FULLNAME = "fullname"
    public var EMAIL = "email"
    public var PHONE = "phone"
    public var PHOTO = "photo"

    private var databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance("https://gymapp-386117-default-rtdb.europe-west1.firebasedatabase.app").reference

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

        //TODO Email Sign In

        val email = binding.email
        val password = binding.password
        val loginBtn = binding.loginBtn
        val registerNowBtn = binding.registerNowBtn

        loginBtn.setOnClickListener{
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            val hashedEmail = HashUtils.sha256(emailTxt)

            if (emailTxt.isEmpty() || passwordTxt.isEmpty()){
                Toast.makeText(this@LoginActivity, "Please enter your email or password", Toast.LENGTH_SHORT).show()
            }
            else{
                databaseReference.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.hasChild(hashedEmail)){
                            val getPassword = snapshot.child(hashedEmail).child("password").value

                            if (getPassword!! == passwordTxt){
                                Toast.makeText(this@LoginActivity, "Successfully Logged in", Toast.LENGTH_SHORT).show()

                                val bundle = Bundle()

                                bundle.putString(FULLNAME, snapshot.child(hashedEmail).child("fullname").value.toString())
                                bundle.putString(PHONE, snapshot.child(hashedEmail).child("mobile").value.toString())
                                bundle.putString(EMAIL, snapshot.child(hashedEmail).child("email").value.toString())

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtras(bundle)

                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(this@LoginActivity, "Wrong Password", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(this@LoginActivity, "Wrong Email", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }

        registerNowBtn.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //TODO Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        var googleBtn = binding.googleLogin
        googleBtn.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(FULLNAME, account.displayName)
                intent.putExtra(PHOTO, account.photoUrl)
                intent.putExtra(EMAIL, account.email)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}