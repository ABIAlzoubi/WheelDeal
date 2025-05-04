package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nl.joery.animatedbottombar.AnimatedBottomBar

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class home_page : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val doubleBackToExitThreshold = 2000 // 2 seconds
    object G{
        var posts_list= mutableListOf<posts>()
    }
    lateinit var rv:RecyclerView
    lateinit var post_adapter: add_post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //Todo creating post adapter
        EventChangeListener()
        rv=findViewById(R.id.rv)
        post_adapter=add_post(this,G.posts_list)
        rv.adapter=post_adapter
        rv.layoutManager= LinearLayoutManager(this)

        //Todo post on click listener
        post_adapter.onItemClick={
            val x= Intent(this,car_info_page::class.java)
            startActivity(x)
            val i= Intent(this,car_info_page::class.java)
            i.putExtra("ob",it)
            startActivity(i)
        }

        //Bottom Navigation Bar handling
        val animatedBottomBar = findViewById<AnimatedBottomBar>(R.id.animatedBottomBar)
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
                //R.id.tab_home->Global.G_Bottom_Tab="home_page"

                else -> false
            }
        }
        val nestedScrollView=findViewById<NestedScrollView>(R.id.nestedScrollView)
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
                //R.id.tab_home->Global.G_Bottom_Tab="home_page"
                else -> false
            }
        }

    }

    //Todo finction for Getting data from the firebase
    @SuppressLint("NotifyDataSetChanged")
    private fun EventChangeListener() {
        val db = FirebaseFirestore.getInstance()
        db.collection("cars")
            .get()
            .addOnSuccessListener { result ->
                G.posts_list.clear()
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
                        G.posts_list.add(ob)
                    }
                }
                post_adapter.notifyDataSetChanged()
            }

            .addOnFailureListener { exception ->
                // Handle failure
                println(exception)
            }
    }

    // Todo function to calculate time when the user get out of the view
//    override fun onPause() {
//        super.onPause()
//        val calendar = Calendar.getInstance()
//        MainActivity.clock1.difhours+=Math.abs(calendar.get(Calendar.HOUR_OF_DAY)-MainActivity.clock1.hours)
//        MainActivity.clock1.difminutes+=Math.abs(calendar.get(Calendar.MINUTE)-MainActivity.clock1.minutes)
//        MainActivity.clock1.difseconds+=Math.abs(calendar.get(Calendar.SECOND)-MainActivity.clock1.seconds)
//        MainActivity.clock1.hours = calendar.get(Calendar.HOUR_OF_DAY)
//        MainActivity.clock1.minutes = calendar.get(Calendar.MINUTE)
//        MainActivity.clock1.seconds = calendar.get(Calendar.SECOND)
//    }


    override fun onResume() {
        super.onResume()
        EventChangeListener()
    }

    //Todo Back Button handling
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedTime + doubleBackToExitThreshold > System.currentTimeMillis()) {
            //Todo If back button is pressed again within 2 seconds, treat it as a double click
            super.onBackPressed()
            finishAffinity() // Close the app
            return
        } else {
            Toast.makeText(
                this,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}