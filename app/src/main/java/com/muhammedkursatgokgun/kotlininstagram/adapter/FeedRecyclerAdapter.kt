package com.muhammedkursatgokgun.kotlininstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammedkursatgokgun.kotlininstagram.databinding.RecyclerRowBinding
import com.muhammedkursatgokgun.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postlist : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postlist.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val part = postlist.get(position).email
        val kes = "@"
        val isim = part.split(kes).toMutableList()
        isim[0] = isim.get(0).capitalize()
        holder.binding.recyclerEmailTextView.text = isim.get(0)
        holder.binding.recyclerCommentText.text = postlist.get(position).comment
        Picasso.get().load(postlist.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }
}