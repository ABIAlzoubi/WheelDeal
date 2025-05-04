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
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import nl.joery.animatedbottombar.AnimatedBottomBar

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class notifi_page : AppCompatActivity() {
    object Global5 {
        var notifi_list = mutableListOf<Notifi_class>()
    }
    lateinit var post_adapter4: add_notifi
    lateinit var rv4:RecyclerView
    lateinit var nothingPic3:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifi_page)


        rv4 =findViewById(R.id.rv4)
        nothingPic3=findViewById(R.id.nothing_pic3)
        val animatedBottomBar = findViewById<AnimatedBottomBar>(R.id.animatedBottomBar)
        val nestedScrollView=findViewById<NestedScrollView>(R.id.nestedScrollView)
        val animatedtopBar = findViewById<AnimatedBottomBar>(R.id.animatedTopBar)


        //Todo creating post adapter
        EventChangeListener()

            EventChangeListener()
            rv4.visibility = View.VISIBLE
            nothingPic3.visibility= View.INVISIBLE
            post_adapter4 = add_notifi(this, Global5. notifi_list)
            rv4.adapter = post_adapter4
            rv4.layoutManager = LinearLayoutManager(this)

            // Todo posts on click listener
            post_adapter4.onItemClick={
                val x= Intent(this,user_info::class.java)
                startActivity(x)
                val i= Intent(this,user_info::class.java)
                i.putExtra("ob",it)
                startActivity(i)}



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
    @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
    fun EventChangeListener() {
        val db = FirebaseFirestore.getInstance()
        val uid = com.google.firebase.Firebase.auth.currentUser

        db.collection("notifi").whereEqualTo("user_id" , uid?.email.toString())
            .get()
            .addOnSuccessListener { result ->
                Global5.notifi_list.clear()
                if (result.isEmpty) {
                    rv4.visibility = View.INVISIBLE
                    nothingPic3.visibility = View.VISIBLE
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
                        val id = document.data["id"].toString()
                        val user_id = document.data["user_id"].toString()

                        val ob = Notifi_class(brand, type, model, contactnumber, startingprice.toInt(),
                            city,notes, pic, day.toInt(), hour,minute,carNumber,currentPrice.toInt(),id,user_id)

                        Global5.notifi_list.add(ob)
                    }
                }
                post_adapter4.notifyDataSetChanged()


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