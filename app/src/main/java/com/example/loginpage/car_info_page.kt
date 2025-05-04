package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.ContextThemeWrapper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import nl.joery.animatedbottombar.AnimatedBottomBar

@Suppress("DEPRECATION", "UNUSED_EXPRESSION", "SENSELESS_COMPARISON")
class car_info_page : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    val uid=Firebase.auth.currentUser
    val db = Firebase.firestore
    object clock{
        var hours=""
        var minutes=""
        var carnum=""
        var hour:Long=0
        var min:Long=0
        lateinit var carInfo2:posts
    }

    lateinit var carInfo:posts
    var user_id:String=""
    var user:String=""
    var currentPrice:Int=0
    var time :Long=0
    var seconds:Long=0
    var remainingSeconds:Long=0
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_info_page)

        //todo Ui component declaration
        val nestedScrollView=findViewById<NestedScrollView>(R.id.nestedScrollView)
        val imgpreview=findViewById<ImageView>(R.id.img_preview)
        val carBrandPreview=findViewById<TextView>(R.id.Car_Brand_Preview)
        val carTypePreview=findViewById<TextView>(R.id.Car_Type_Preview)
        val carModelPreview=findViewById<TextView>(R.id.Car_Model_Preview)
        val carCityPreview=findViewById<TextView>(R.id.Car_City_Preview)
        val carNotesPreview=findViewById<TextView>(R.id.Car_Notes_Preview)
        val carStartingPricePreview=findViewById<TextView>(R.id.Car_StartingPrice_Preview)
        val phonePreview=findViewById<TextView>(R.id.Phone_Preview)
        val carCurrentPricePreview=findViewById<TextView>(R.id.Car_CurrentPrice_Preview)
        val auctionBtn=findViewById<AppCompatButton>(R.id.Auction_btn)
        val hoursPreview=findViewById<TextView>(R.id.hours_Preview)
        val minutesPreview=findViewById<TextView>(R.id.minutes_Preview)
        val secondsPreview=findViewById<TextView>(R.id.seconds_Preview)
        val daysPreview=findViewById<TextView>(R.id.Days_Preview)


        //getting car data from home_page
         carInfo=intent.getSerializableExtra("ob") as posts
        if (carInfo != null) {

        clock.carInfo2=carInfo
        EventChangeListener(carInfo.carNumber)
        clock.carnum=carInfo.carNumber
            //Todo setting Image
            val storage = FirebaseStorage.getInstance()
            // Reference to image in Firebase Storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("//images/${carInfo.picture.toUri().lastPathSegment}")

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Load the image into the ImageView using Glide
                Glide.with(this)
                    .load(uri)
                    .into(imgpreview)
            }.addOnFailureListener { }

            //setting Brand
            carBrandPreview.text=carInfo.Brand

            //setting Type
            carTypePreview.text=carInfo.type

            //setting Model
            carModelPreview.text=carInfo.model

            //setting City
            carCityPreview.text=carInfo.city

            //setting Note
            carNotesPreview.text=carInfo.notes

            //setting Starting Price
            carStartingPricePreview.text="${carInfo.price} JOD"

            //setting Phone Number
            phonePreview.text=carInfo.phone

            //setting days
            daysPreview.text=carInfo.Auction_day.toString()

            //Todo setting Hours
//            val calendar = Calendar.getInstance()
//            clock.hour = Math.abs(calendar.get(Calendar.HOUR).toLong() - MainActivity.clock1.hours.toLong())// 12-hour format
//            clock.min= Math.abs(calendar.get(Calendar.MINUTE).toLong()- MainActivity.clock1.minutes.toLong())
//
            clock.hour=carInfo.Auction_hour.toLong()*3600 * 1000
            clock.min=carInfo.Auction_minutes.toLong()*60 * 1000
            time=clock.hour+clock.min

            if(carInfo.Auction_day==0&&time.toInt()==0) {
                hoursPreview.text = "0"
                minutesPreview.text = "0"
                secondsPreview.text = "0"
                auctionBtn.isEnabled=false

            }else {
                object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        seconds = millisUntilFinished / 1000
                        clock.hours = (seconds / 3600).toString()
                        remainingSeconds = seconds % 3600
                        clock.minutes = (remainingSeconds / 60).toString()
                        remainingSeconds %= 60
                        hoursPreview.text = clock.hours
                        minutesPreview.text = clock.minutes
                        secondsPreview.text = remainingSeconds.toString()
                    }

                    //Todo when the time is up
                    @SuppressLint("SuspiciousIndentation")
                    override fun onFinish() {
                        EventChangeListener(carInfo.carNumber)
                        var auctionDays = carInfo.Auction_day
                        if (auctionDays != 0) {
                            auctionDays -= 1
                            daysPreview.text = auctionDays.toString()
                            onTick(24 * 3600000)
                        } else {
                            udpate_time(carInfo.carNumber, "0", "0")
                            add_notifi(carInfo.Brand, carInfo.type, carInfo.model, carInfo.phone, carInfo.price, carInfo.city,
                                carInfo.notes, carInfo.picture, carInfo.Auction_day, "0",
                                "0", carInfo.carNumber,currentPrice, user_id, user)
                            delete_car(carInfo.carNumber)
                        }
                    }
                }.start()
            }

            //setting Current Price
            carCurrentPricePreview.text="${carInfo.currentPrice} JOD"

            //Todo popup dialog
            currentPrice=carInfo.currentPrice
            val dialog= AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Sure you want to Bid?")
                .setMessage("")
                .setPositiveButton("Yes"){_,_->
                    currentPrice+=100
                    carCurrentPricePreview.text="${currentPrice}JOD"

                    //Todo checking if the car exists in the users_auction collection collection
                    val db = FirebaseFirestore.getInstance()
                    val carsCollectionRef = db.collection("users_auction")
                    carsCollectionRef
                        .whereEqualTo("CarNumber", carInfo.carNumber)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                //Todo adding the car to the users_auction collection
                                    add_auction_car(carInfo.Brand,carInfo.type,carInfo.model,carInfo.phone,carInfo.price,carInfo.city,carInfo.notes,
                                        carInfo.picture,carInfo.Auction_day,carInfo.Auction_hour,carInfo.Auction_minutes,carInfo.carNumber,carInfo.currentPrice)
                            }
                        }
                    //Todo calling the update price function
                    priceUpdate(carInfo.carNumber,currentPrice)
                    user=uid?.email.toString()
                }
                .setNeutralButton("No") { _,_ ->

                }
                .create()

            //Todo Setting Auction button
            lateinit var gestureDetector: GestureDetector
            gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

                    //Todo checking who is the last user Bid on this car and if the car owner trying to bid on his car
                    val db = FirebaseFirestore.getInstance()
                    db.collection("cars").whereEqualTo("CarNumber" , carInfo.carNumber)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if (document != null){
                                    if (document.data["id"].toString() == uid?.email.toString() || document.data["user_id"].toString() == uid?.email.toString())
                                        you_can_not_Message()
                                    else {
                                        dialog.show()
                                    }
                                }
                            }
                        }

                    return true
                }
                override fun onDown(e: MotionEvent): Boolean {
                    auctionBtn.alpha = 0.4f
                    return true
                }
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    auctionBtn.alpha = 0.9f
                    return true
                }
            })
            auctionBtn.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event)}




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
            } }
    }

    //Todo function to add the car you Bid on to the User_action collection
    fun add_auction_car(
        etcarBrand: String, etcarType: String, tvcarModel: String, etphoneNum: String, etstartinPrice: Int,
        spCities: String, tvNotes: String, pic: String, days: Int, hours: String, minutes: String,carNumber:String,currentPrice: Int
    ) {
        val car = hashMapOf(
            "user_id" to uid?.email.toString(), "user" to "auction", "brand" to etcarBrand, "type" to etcarType,
            "model" to tvcarModel,"contactnumber" to etphoneNum, "startingprice" to etstartinPrice, "city" to spCities,
            "Note" to tvNotes, "img" to pic, "day" to days, "hour" to hours, "minute" to minutes,"CarNumber" to carNumber,"currentPrice" to currentPrice
        )
        val usercar = db.collection("users_auction")
        usercar.add(car)
    }

    //Todo function to add the car you Bid on to the notifi collection
    fun add_notifi(
        etcarBrand: String, etcarType: String, tvcarModel: String, etphoneNum: String, etstartinPrice: Int,
        spCities: String, tvNotes: String, pic: String, days: Int, hours: String, minutes: String,carNumber:String,currentPrice: Int,user_id:String,id:String
    ) {
        val car = hashMapOf(
            "user_id" to user_id, "user" to "auction", "brand" to etcarBrand, "type" to etcarType,
            "model" to tvcarModel,"contactnumber" to etphoneNum, "startingprice" to etstartinPrice, "city" to spCities,
            "Note" to tvNotes, "img" to pic, "day" to days, "hour" to hours, "minute" to minutes,"CarNumber" to carNumber,"currentPrice" to currentPrice
            ,"id" to id
        )
        val usercar = db.collection("notifi")
        usercar.add(car)
    }


    //Todo function to update the price in the fireStore
    fun priceUpdate(carNum:String,currentPrice:Int){
        val collectionReference = db.collection("cars")
        // Perform a query to find the document with a specific field value
        collectionReference.whereEqualTo("CarNumber",carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        val mapof = mapOf(
                            "currentPrice" to currentPrice,
                            "id" to uid?.email.toString()
                        )
                        db.collection("cars").document(documentName)
                            .update(mapof)
                    }
                } else {
                    println("Error getting documents: ${task.exception}")
                }
            })
        val collectionReference2 = db.collection("users_auction")
        // Perform a query to find the document with a specific field value
        collectionReference2.whereEqualTo("CarNumber", carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        val mapof = mapOf(
                            "currentPrice" to currentPrice,
                            "id" to uid?.email.toString()
                        )
                        db.collection("users_auction").document(documentName)
                            .update(mapof)
                    }
                }
            })
        val collectionReference3 = db.collection("users_add")
        // Perform a query to find the document with a specific field value
        collectionReference3.whereEqualTo("CarNumber", carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        println("Document Name: $documentName")
                        val mapof = mapOf(
                            "currentPrice" to currentPrice,
                            "id" to uid?.email.toString()
                        )
                        db.collection("users_add").document(documentName)
                            .update(mapof)
                    }
                }
            })
    }


    //Todo function to update the Time in the fireStore
    fun udpate_time(carNum:String,hours: String,minutes: String){
        val collectionReference1 = db.collection("cars")
        // Perform a query to find the document with a specific field value
        collectionReference1.whereEqualTo("CarNumber", carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        println("Document Name: $documentName")
                        val mapof = mapOf(
                            "hour" to hours,
                            "minute" to minutes
                        )
                        db.collection("cars").document(documentName)
                            .update(mapof)
                    }
                }
            })
        val collectionReference2 = db.collection("users_add")
        // Perform a query to find the document with a specific field value
        collectionReference2.whereEqualTo("CarNumber", carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        println("Document Name: $documentName")
                        val mapof = mapOf(
                            "hour" to hours,
                            "minute" to minutes
                        )
                        db.collection("users_add").document(documentName)
                            .update(mapof)
                    }
                }
            })
        val collectionReference3 = db.collection("users_auction")
        // Perform a query to find the document with a specific field value
        collectionReference3.whereEqualTo("CarNumber", carNum)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        println("Document Name: $documentName")
                        val mapof = mapOf(
                            "hour" to hours,
                            "minute" to minutes
                        )
                        db.collection("users_auction").document(documentName)
                            .update(mapof)
                    }
                }
            })
    }

    // Todo function to calculate time when the user get out of the view
    @SuppressLint("SuspiciousIndentation")
    override fun onPause() {
        super.onPause()
//            val calendar = Calendar.getInstance()
//            MainActivity.clock1.difhours+=Math.abs(calendar.get(Calendar.HOUR_OF_DAY)-MainActivity.clock1.hours)
//            MainActivity.clock1.difminutes+=Math.abs(calendar.get(Calendar.MINUTE)-MainActivity.clock1.minutes)
//            MainActivity.clock1.difseconds+=Math.abs(calendar.get(Calendar.SECOND)-MainActivity.clock1.seconds)
//            MainActivity.clock1.hours = calendar.get(Calendar.HOUR_OF_DAY)
//            MainActivity.clock1.minutes = calendar.get(Calendar.MINUTE)
//            MainActivity.clock1.seconds = calendar.get(Calendar.SECOND)
    }


//    override fun onResume() {
//        super.onResume()
//        clock.count=0
//
//        val calendar = Calendar.getInstance()
//        clock.hour = Math.abs(calendar.get(Calendar.HOUR).toLong() - MainActivity.clock1.hours.toLong())// 24-hour format
//        clock.min= Math.abs(calendar.get(Calendar.MINUTE).toLong()- MainActivity.clock1.minutes.toLong())
//
//        clock.hour=Math.abs(clock.carInfo2.Auction_hour.toLong()-clock.hour)*3600 * 1000
//        clock.min=Math.abs(clock.carInfo2.Auction_minutes.toLong()-clock.min)*60 * 1000
//
//        time=clock.hour+clock.min
//
//
//        seconds=time / 1000
//        clock.hours = (seconds / 3600).toString()
//        remainingSeconds = seconds % 3600
//        clock.minutes = (remainingSeconds / 60).toString()
//        remainingSeconds %= 60
//    }

    fun you_can_not_Message() {
        Toast.makeText(this, "Failure updated you can not", Toast.LENGTH_SHORT).show()
    }



  //Todo function to get the owner of this car
    @SuppressLint("SuspiciousIndentation")
    fun EventChangeListener(carNum: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("cars").whereEqualTo("CarNumber",carNum)
            .get()
            .addOnSuccessListener { it ->
                for (document in it) {
                    if (document!=null) {
                        user_id = document.data["user_id"].toString()
 //                       user = document.data["user"].toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                println(exception)
            }
    }


    //Todo functions to delete the care from the cars collection when time is up
    fun del(doc:String ){
        val car = hashMapOf<String, Any>(
            "user_id" to  FieldValue.delete(), "user" to  FieldValue.delete(), "brand" to  FieldValue.delete(), "type" to  FieldValue.delete(),
            "model" to  FieldValue.delete(),"contactnumber" to  FieldValue.delete(), "startingprice" to  FieldValue.delete(), "city" to  FieldValue.delete(),
            "Note" to  FieldValue.delete(), "img" to  FieldValue.delete(), "day" to  FieldValue.delete(), "hour" to  FieldValue.delete(),
            "minute" to  FieldValue.delete(),"CarNumber" to  FieldValue.delete(),"currentPrice" to  FieldValue.delete(),"id" to FieldValue.delete()
        )
        db.collection("cars").document(doc)
            .delete()
    }
    fun delete_car(carnum:String){
        db.collection("cars").whereEqualTo("CarNumber",carnum)
            // Perform a query to find the document with a specific field value
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentName = document.id
                        del(documentName)
                    }
                }
            })

    }


    //Todo Back Button handling
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        if (clock.hours!="0"&&clock.minutes!="0")
            udpate_time( clock.carnum,clock.hours,clock.minutes)
        startActivity(Intent(this, home_page::class.java))
        finish()
    }
}
