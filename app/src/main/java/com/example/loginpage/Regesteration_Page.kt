package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doOnTextChanged
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Suppress("DEPRECATION")
class Regesteration_Page : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    object check{
        var chekPass=false
        var chekPhone=false
        var checkName=false
        var conChek=false
        var UserPassword=""
        var UserName=""
    }
    val db=Firebase.firestore
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regesteration_page)

        val auth= Firebase.auth
        //Todo Ui component declaration
        val Name=findViewById<EditText>(R.id.edName)
        val nameError=findViewById<TextView>(R.id.tvNameError)
        val nameEmpty=findViewById<TextView>(R.id.tvNameEmpty)
        val pass=findViewById<EditText>(R.id.edPassword2)
        val passError=findViewById<TextView>(R.id.pssError2)
        val passCon =findViewById<EditText>(R.id.edConfirmedPassword)
        val btnSignUp=findViewById<AppCompatButton>(R.id.btnSignUp)
        val ConError=findViewById<TextView>(R.id.ConError)
        val phone=findViewById<EditText>(R.id.edphone)


        //Todo Name field change on text change
        Name.doOnTextChanged { _, _, _, _ ->
            if (Name.text.toString().isBlank()) {
                nameEmpty.visibility = View.VISIBLE
                nameError.visibility= View.INVISIBLE
                check.checkName=false
            }else {
                nameEmpty.visibility = View.INVISIBLE
                if ("@" in Name.text.toString() && ".com" in Name.text.toString())
                    nameError.visibility= View.INVISIBLE
                else
                    nameError.visibility= View.VISIBLE
                check.checkName=true
            }
            check.UserName=Name.text.toString()
        }


        //Todo Password field change on text change
        pass.doOnTextChanged { _, _, _, _ ->
            val charactures= "@#$%&*_/"
            if (charactures.any {pass.text.toString().contains(it)}){
                passError.visibility = View.INVISIBLE
                check.chekPass=true
            }else {
                passError.visibility = View.VISIBLE
                check.chekPass=false
            }
            if (pass.text.toString().length <= 8)
                check.conChek = false
            check.UserPassword=pass.text.toString()
        }



        //Todo Password Confirmation field change on text change
        passCon.doOnTextChanged { _, _, _, _ ->
            if (check.UserPassword != passCon.text.toString()) {
                ConError.visibility = View.VISIBLE
                check.conChek = false
            }
            if(check.UserPassword == passCon.text.toString()) {
                ConError.visibility = View.INVISIBLE
                check.conChek = true
            }
        }


        //Todo Setting SignUp button
        lateinit var gestureDetector: GestureDetector
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                //Todo Phone number handling
                isValidPhoneNumber(phone.text.toString())
                val users= hashMapOf(
                    "user_email" to check.UserName,
                    "phone_number" to phone.text.toString()
                )



                if (check.checkName && check.chekPass && check.conChek && check.chekPhone){
                    auth.createUserWithEmailAndPassword(check.UserName,check.UserPassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                move_to_home_page()
                                db.collection("users")
                                    .add(users)
                            }
                            else{
                                massage()
                            }

                        }
                }
                return true
            }
            override fun onDown(e: MotionEvent): Boolean {
                btnSignUp.alpha = 0.4f
                return true
            }
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                btnSignUp.alpha = 0.9f
                return true
            }
        })
        btnSignUp.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

    }

    //Todo function to move to the home page
    fun move_to_home_page(){
        val i= Intent(this,home_page::class.java)
        startActivity(i)
    }
    //Todo function that give a Toast
    fun massage(){
        Toast.makeText(
            this, "incorrect username or password",Toast.LENGTH_SHORT).show()
    }


    //Todo function to check if the Phone number is valid
    fun isValidPhoneNumber(phoneNumber: String){
        val pattern = Regex("^07\\d{8}\$")  // Regex pattern for '07' followed by 8 digits
        if ( pattern.matches(phoneNumber))
            check.chekPhone=true
        else{
            check.chekPhone=false
            Toast.makeText(this,"Phone number is not valid",Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}

