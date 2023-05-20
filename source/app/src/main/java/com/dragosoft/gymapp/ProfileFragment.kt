package com.dragosoft.gymapp

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.gymapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var imageView: ImageView
    lateinit var button: Button
     var  dataBundle: Bundle? = null
    private val REQUEST_IMAGE_CAPTURE = 100

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater,container, false)

        auth = Firebase.auth

        binding.googleSignout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }

        val name = arguments?.getString(FULLNAME)
        val email = arguments?.getString(EMAIL)
        val phone = arguments?.getString(PHONE)
        val photo = arguments?.getString(PHOTO)

        imageView = binding.profilePicture
        button = binding.buttonChangePicture


        updateData(name, email, phone, photo)
        takePicture()

        return binding.root
    }

    private fun updateData(name: String?, email: String?, phone: String?, photo: String?) {
        if (!name.isNullOrEmpty()){
            binding.name.text = "Name: $name\n"
        }else{
            binding.name.text = "Name: Unknown\n"
        }
        if (!email.isNullOrEmpty()){
            binding.email.text = "Email: $email\n"
        }else{
            binding.email.text = "Email: Unknown\n"
        }
        if (!phone.isNullOrEmpty()){
            binding.phone.text = "Phone: $phone\n"
        }else{
            binding.phone.text = "Phone: Unknown\n"
        }

        if (!photo.isNullOrEmpty()){
            Glide.with(requireContext())
                .load(Uri.parse(photo))
                .into(binding.profilePicture)
        }
    }

    companion object {
        const val FULLNAME = "fullname"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val PHOTO = "photo"

        fun newInstance(name: String?, email: String?, phone:String?, photo:String?): ProfileFragment {
            val fragment = ProfileFragment()

            val bundle = Bundle().apply {
                putString(FULLNAME, name)
                putString(EMAIL, email)
                putString(PHONE, phone)
                putString(PHOTO, photo)
            }

            fragment.arguments = bundle

            return fragment
        }
    }

    private fun takePicture(){
        button.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }catch (e:ActivityNotFoundException){
                Toast.makeText(context, "Error" + e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}