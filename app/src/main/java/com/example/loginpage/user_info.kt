package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import nl.joery.animatedbottombar.AnimatedBottomBar

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class user_info : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val animatedBottomBar = findViewById<AnimatedBottomBar>(R.id.animatedBottomBar)
        val prPic = findViewById<ImageView>(R.id.img_preview_user)
        val prPrice = findViewById<TextView>(R.id.et_lastPrice)
        val prEmail = findViewById<TextView>(R.id.et_lastPerson)
        val prPhone = findViewById<TextView>(R.id.et_LastPhone)


        val carInfo = intent.getSerializableExtra("ob") as? Notifi_class
        if (carInfo != null) {
            fun prPhoneNumber(txt:String){
                prPhone.text=txt
            }
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("user_email",carInfo.user)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document != null) {
                            prPhoneNumber(document.data["phone_number"].toString())
                        }
                        else
                            prPhoneNumber("No Bids Yet")
                    }
                }

            val storage = FirebaseStorage.getInstance()
            // Reference to image in Firebase Storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("//images/${carInfo.picture.toUri().lastPathSegment}")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Load the image into the ImageView using Glide
                Glide.with(this)
                    .load(uri)
                    .into(prPic)

            }

            prPrice.text = carInfo.currentPrice.toString()

            if (carInfo.user=="add")
                prEmail.text="No Bids Yet"
            else
                prEmail.text=carInfo.user

        }



        //Bottom Navigation Bar handling
        animatedBottomBar.onTabSelected = {
            when(it.id){
                R.id.tab_add-> {
                    startActivity(Intent(this, add_car_page::class.java))
                    true
                }
                R.id.tab_home-> {
                    startActivity(Intent(this, home_page::class.java))
                    true
                }
                R.id.tab_my_auction-> {
                    startActivity(Intent(this, my_auction_page::class.java))
                    true
                }
                else -> false
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, notifi_page::class.java))
    }

}
