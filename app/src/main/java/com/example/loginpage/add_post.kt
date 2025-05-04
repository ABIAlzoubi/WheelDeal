package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


class add_post(private val context: Context, var posts:List<posts>) :RecyclerView.Adapter<add_post.PostViewHolder>() {


    var onItemClick: ((posts) -> Unit)? = null

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1: TextView = itemView.findViewById(R.id.car_name)
        val tv2: TextView = itemView.findViewById(R.id.car_type)
        val tv3: TextView = itemView.findViewById(R.id.car_model)
        val tv4: TextView = itemView.findViewById(R.id.timer)
        val tv5: TextView = itemView.findViewById(R.id.car_price)
        val pic: ImageView = itemView.findViewById(R.id.car_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.apply {
            tv1.text = currentPost.Brand
            tv2.text = currentPost.type
            tv3.text = currentPost.model
            tv4.text = "${currentPost.Auction_day} Days left"
            tv5.text = "${currentPost.currentPrice} JOD"


            val storage = FirebaseStorage.getInstance()
            // Reference to your image in Firebase Storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("//images/${currentPost.picture.toUri().lastPathSegment}") // Replace with your image path
            // Get the download URL for the image
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Load the image into the ImageView using Glide
                Glide.with(context)
                    .load(uri)
                    .into(pic)
            }.addOnFailureListener { exception ->
                // Handle any errors
                exception.printStackTrace()
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(currentPost)
            }
        }

    }
}
