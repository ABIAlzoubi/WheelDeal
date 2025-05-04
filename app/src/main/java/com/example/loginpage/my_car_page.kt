package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import nl.joery.animatedbottombar.AnimatedBottomBar

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class my_car_page : AppCompatActivity() {

    object Global3 {
        var mycar_list = mutableListOf<posts>()
    }
    lateinit var post_adapter3: add_post
    lateinit var rv3:RecyclerView
    lateinit var nothingPic2:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_car_page)

        //Todo Ui component declaration
        nothingPic2=findViewById<ImageView>(R.id.nothing_pic2)
        rv3=findViewById(R.id.rv3)
        val nestedScrollView=findViewById<NestedScrollView>(R.id.nestedScrollView)
        val animatedtopBar = findViewById<AnimatedBottomBar>(R.id.animatedTopBar)
        val animatedBottomBar = findViewById<AnimatedBottomBar>(R.id.animatedBottomBar)


        //Todo creating post adapter
        EventChangeListener()

            EventChangeListener()
            rv3.visibility = View.VISIBLE
            nothingPic2.visibility = View.INVISIBLE
            post_adapter3 = add_post(this, Global3.mycar_list)
            rv3.adapter = post_adapter3
            rv3.layoutManager = LinearLayoutManager(this)

            // Todo posts on click listener
            post_adapter3.onItemClick={
                val x= Intent(this,car_info_page::class.java)
                startActivity(x)
                val i= Intent(this,car_info_page::class.java)
                i.putExtra("ob",it)
                startActivity(i) }



        //Top Navigation Bar handling
        animatedtopBar.onTabSelected = {
            when(it.id){
                R.id.tab_My_Bids-> {
                    startActivity(Intent(this, my_auction_page::class.java))
                    true
                }
                R.id.tab_My_Car-> {
                    startActivity(Intent(this, my_car_page::class.java))
                    true
                }
                R.id.tab_notifi-> {
                    startActivity(Intent(this, notifi_page::class.java))
                    true
                }
                else -> false
            }
        }
        animatedtopBar.onTabReselected = {
            when(it.id){
                R.id.tab_My_Bids-> {
                    if (it.id == animatedtopBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_My_Car-> {
                    if (it.id == animatedtopBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_notifi-> {
                    if (it.id == animatedtopBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                else -> false
            }
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
        animatedBottomBar.onTabReselected = {
            when(it.id){
                R.id.tab_add-> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_home-> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_my_auction-> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    //Todo function to get all the cars the current user has added
    @SuppressLint("NotifyDataSetChanged")
    fun EventChangeListener() {
        val db = FirebaseFirestore.getInstance()
        val uid= Firebase.auth.currentUser
        db.collection("users_add").whereEqualTo("user_id" , uid?.email.toString())
            .get()
            .addOnSuccessListener { result ->
                Global3.mycar_list.clear()
                if (result.isEmpty) {
                    rv3.visibility = View.INVISIBLE
                    nothingPic2.visibility = View.VISIBLE
                }
                for (document in result) {
                    if (document != null) {
                        val brand = document.data["brand"].toString()
                        val type = document.data["type"].toString()
                        val model = document.data["model"].toString()
                        val contactnumber = document.data["contactnumber"].toString()
                        val startingprice = document.data["startingprice"].toString()
                        val city = document.data["city"].toString()
                        val notes= document.data["Note"].toString()
                        val pic= document.data["img"].toString()
                        val day = document.data["day"].toString()
                        val hour = document.data["hour"].toString()
                        val minute = document.data["minute"].toString()
                        val carNumber = document.data["CarNumber"].toString()
                        val currentPrice = document.data["currentPrice"].toString()

                        val ob = posts(brand, type, model, contactnumber, startingprice.toInt(),
                            city,notes, pic, day.toInt(), hour,minute,carNumber,currentPrice.toInt())

                        Global3.mycar_list.add(ob)
                    }
                }
                    post_adapter3.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println(exception)
            }
    }

    override fun onResume() {
        super.onResume()
        EventChangeListener()
    }


    //Back Button handling
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, my_auction_page::class.java))
    }
}