package com.muhammedkursatgokgun.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muhammedkursatgokgun.kotlininstagram.R
import com.muhammedkursatgokgun.kotlininstagram.adapter.FeedRecyclerAdapter
import com.muhammedkursatgokgun.kotlininstagram.databinding.ActivityFeedBinding
import com.muhammedkursatgokgun.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var postAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)
        auth= Firebase.auth
        db = Firebase.firestore
        postArrayList=ArrayList<Post>()
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = postAdapter
    }

    private fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if(firebaseFirestoreException!=null){
                Toast.makeText(this,firebaseFirestoreException.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(querySnapshot!=null&& !querySnapshot.isEmpty){

                    val documents = querySnapshot.documents
                    for (document in documents){
                        val comment = document.get("comment") as String // string gibi düşün ;)
                        val userEmail = document.get("userEmail") as String
                        val docUrl = document.get("downloadUrl") as String
                        val post = Post(userEmail,comment,docUrl)
                        postArrayList.add(post)
                        //println(comment)
                    }
                    postAdapter.notifyDataSetChanged()

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater= getMenuInflater()
        menuInflater.inflate(R.menu.insta_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId== R.id.add_post){
            val intent= Intent(this, UploadActivty::class.java)
            startActivity(intent)
        }else if(item.itemId== R.id.sign_out){
            auth.signOut()
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}