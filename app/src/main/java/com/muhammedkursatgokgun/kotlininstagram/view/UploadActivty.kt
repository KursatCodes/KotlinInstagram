package com.muhammedkursatgokgun.kotlininstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.muhammedkursatgokgun.kotlininstagram.databinding.ActivityUploadActivtyBinding
import java.util.UUID

class UploadActivty : AppCompatActivity() {
    private lateinit var binding: ActivityUploadActivtyBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage


    private lateinit var activityResultLaunher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadActivtyBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        auth= Firebase.auth
        storage = Firebase.storage
        firestore = Firebase.firestore

        registerLauncher()
    }
    fun selectImage(view:View){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery.",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLaunher.launch(intentToGallery)
        }
    }

    fun uploadClicked(view: View){

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val referance = storage.reference.child("images").child(imageName)
        if(selectedPicture!=null){
            referance.putFile(selectedPicture!!).addOnSuccessListener {
                //download url -> firestore
                val updatedImageUrl = storage.reference.child("images").child(imageName)
                updatedImageUrl.downloadUrl.addOnSuccessListener{
                    val downloadUrl = it.toString()
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("comment",binding.editTextComment.text.toString())
                    postMap.put("date",Timestamp.now())

                    firestore.collection("Posts").add(postMap).addOnSuccessListener {
                        //Toast.makeText(this,"Kımooon",Toast.LENGTH_LONG).show()
                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }



                }
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun registerLauncher(){
        activityResultLaunher= registerForActivityResult (ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode== RESULT_OK){
                val intentFromResult= it.data
                if(intentFromResult!= null){
                    selectedPicture= intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher= registerForActivityResult (ActivityResultContracts.RequestPermission()) {
            if (it){
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLaunher.launch(intentToGallery)
            }else{
                Toast.makeText(this,"Needed permission.",Toast.LENGTH_LONG).show()
            }
        }
    }
}