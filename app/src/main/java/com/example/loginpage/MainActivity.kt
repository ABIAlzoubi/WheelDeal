package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private val doubleBackToExitThreshold = 2000 // 2 seconds
    object authen{
        var UserName=""
        var UserPassword=""
    }
//    object clock1{
//        var hours:Int=0
//        var minutes:Int=0
//        var seconds:Int=0
//        var difhours:Int=0
//        var difminutes:Int=0
//        var difseconds:Int=0
//
//    }
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val auth = FirebaseAuth.getInstance()

        //Todo UI component definitions
        val btn=findViewById<AppCompatButton>(R.id.btnSign)
        val Name=findViewById<EditText>(R.id.edName)
        val nameError=findViewById<TextView>(R.id.tvNameError)
        val nameEmpty=findViewById<TextView>(R.id.tvNameEmpty)
        val pass=findViewById<EditText>(R.id.edPassword)
        val passError=findViewById<TextView>(R.id.passError)
        val regesterlink=findViewById<TextView>(R.id.tvlink)


        //getCurrentTime()

        //Todo Name field change on text change
        Name.doOnTextChanged { text, start, before, count ->
            if (Name.text.toString().isBlank())
            {
                nameEmpty.visibility= View.VISIBLE
                nameError.visibility=View.INVISIBLE
            }
            else {
                nameEmpty.visibility = View.INVISIBLE
                if ("@" in Name.text.toString() && ".com" in Name.text.toString())
                    nameError.visibility=View.INVISIBLE
                else
                    nameError.visibility=View.VISIBLE
            }
            authen.UserName=Name.text.toString().trim()
        }


        //Todo Password field change on text change
        pass.doOnTextChanged { _, _, _, _ ->
            val charactures= "@#$%&*_/"
            if (charactures.any{pass.text.toString().contains(it)})
                passError.visibility=View.INVISIBLE
            else {
                passError.visibility = View.VISIBLE
            }
            authen.UserPassword=pass.text.toString().trim()
        }


        //Todo the (Create Now) link
        regesterlink.setOnClickListener{
            val i= Intent(this,Regesteration_Page::class.java)
            startActivity(i)
        }

        //Todo Sign in Button Authentication
        btn.setOnTouchListener(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                btn.alpha = 0.4f
            }
            if (event.action == MotionEvent.ACTION_UP) {
                btn.alpha = 0.9f
            }
            if(authen.UserName.isNotBlank() && authen.UserPassword.isNotBlank()) {
                auth.signInWithEmailAndPassword(authen.UserName, authen.UserPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val i = Intent(this, home_page::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(
                                this,
                                "incorrect username or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            true
        })
    }

    // Todo function to calculate time when the user get into the application
//    fun getCurrentTime() {
//        val calendar = Calendar.getInstance()
//        clock1.hours = calendar.get(Calendar.HOUR) // 24-hour format
//        clock1.minutes = calendar.get(Calendar.MINUTE)
//        clock1.seconds = calendar.get(Calendar.SECOND)
//    }

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
