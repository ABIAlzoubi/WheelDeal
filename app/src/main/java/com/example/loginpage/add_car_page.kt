package com.example.loginpage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.Calendar
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class add_car_page : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    val uid=Firebase.auth.currentUser
    val db = Firebase.firestore

    object Global {
        var G_hour: Int = 0
        var G_minutes: Int = 0
        var G_day: Int = 0
        var G_imgUri: String = ""
        var G_startingPrice: Int = 0
        var G_city: String = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car_page)

    // Todo Ui component declaration
        val nestedScrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        val carPic = findViewById<ImageView>(R.id.add_pic_logo)
        val etcarBrand = findViewById<EditText>(R.id.etcar_brand)
        val etcarType = findViewById<EditText>(R.id.etcar_type)
        val npcarModel = findViewById<NumberPicker>(R.id.npcar_model)
        val tvcarModel = findViewById<TextView>(R.id.tvcar_model)
        val etphoneNum = findViewById<EditText>(R.id.etcontact_number)
        val etstartinPrice = findViewById<EditText>(R.id.etstartin_Price)
        val btnAuctionTime = findViewById<AppCompatButton>(R.id.btnAuction_time)
        val tvAuctionTime = findViewById<TextView>(R.id.tvAuction_time)
        val spCities = findViewById<Spinner>(R.id.spcities)
        val tvNotes = findViewById<EditText>(R.id.tvNotes)
        val etCar_Number = findViewById<EditText>(R.id.etCar_Number)
        val btnsaveCarData = findViewById<AppCompatButton>(R.id.btnsave_car_data)
        val animatedBottomBar = findViewById<AnimatedBottomBar>(R.id.animatedBottomBar)


        //ToDo getting image from Uri gallary
        val changeImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    carPic.setImageURI(imgUri)
                    if (imgUri != null) {
                        Global.G_imgUri = imgUri.toString()
                    }
                }
            }
        carPic.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }


        //ToDo Auction time
        val cal = Calendar.getInstance()
        btnAuctionTime.setOnClickListener {
            var check = ""
            val dpd = DatePickerDialog(
                this@add_car_page,
                { _, year, months, day ->
                    check = "$year-${months + 1}-$day"
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, months, day)
                    }
                    val currentTime = Calendar.getInstance()  //getting system calender
                    if (selectedCalendar.before(currentTime)) {
                        return@DatePickerDialog
                    }
                    // Calculate the difference in milliseconds between the selected date and current date
                    val differenceMillis = selectedCalendar.timeInMillis - currentTime.timeInMillis
                    // Convert milliseconds to days
                    Global.G_day = TimeUnit.MILLISECONDS.toDays(differenceMillis).toInt()

                    //ToDo Time Picker
                    val tdp = TimePickerDialog(
                        this@add_car_page, { _, hour, minute ->
                            if (minute < 10)
                                check += "  ${hour}h: 0${minute}m"
                            else
                                check += "  ${hour}h:${minute}m"
                            Global.G_hour = hour
                            Global.G_minutes = minute
                            tvAuctionTime.text = check
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    )
                    tdp.show()

                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE),
            )
            dpd.show()
        }


        //todo Number picker handling
        npcarModel.minValue = 1960
        npcarModel.maxValue = 2023
        npcarModel.value = 2015
        //ToDo Model
        npcarModel.setOnValueChangedListener { _, _, newval ->
            tvcarModel.text = newval.toString()
        }


        //ToDo city spinner handling
        spCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                Global.G_city = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }



        //ToDo Save button
        btnsaveCarData.setOnClickListener {
            //Brand handling
            var carBrand = etcarBrand.text.toString()

            //Type handling
            var carType = etcarType.text.toString()

            //Starting price
            var startingPrice = etstartinPrice.text.toString()
            if (startingPrice.isNotEmpty()) {
                Global.G_startingPrice = startingPrice.toInt()
            }

            //model
            var carModel = tvcarModel.text.toString()

            //Notes handling
            var note = tvNotes.text.toString()


            //phone Number handling
            var phoneNum = etphoneNum.text.toString()

            //Car Number Handling
            var carNumber= etCar_Number.text.toString()



            //Todo checkinh if the car number exists in the firebase
            val db = FirebaseFirestore.getInstance()
            val carsCollectionRef = db.collection("cars")
            carsCollectionRef
                .whereEqualTo("CarNumber", carNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            val carNumber = document.data?.get("CarNumber").toString()
                            Toast.makeText(this,"Car with CarNumber $carNumber exists!",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {

                        //ToDo checking if all field are filled (except the Notes field and City field)
                        if(carBrand.isBlank()||carType.isBlank()||phoneNum.isBlank()||startingPrice.isBlank()||carModel.isBlank()||carNumber.isBlank()) {
                            Toast.makeText(this, "please fill the Blank field", Toast.LENGTH_LONG).show()
                        }
                        else if(!isValidPhoneNumber(phoneNum)){
                            Toast.makeText(this,"Phone number is not valid",Toast.LENGTH_LONG).show()
                        }
                        else if(!isValidCarNumber(carNumber)){
                            Toast.makeText(this,"Car number is not valid",Toast.LENGTH_LONG).show()
                        }
                        else {
                            val i = Intent(this, home_page::class.java)
                            startActivity(i)


                            //todo Sending data to FireStore
                            add_data(
                                carBrand, carType, carModel, phoneNum, Global.G_startingPrice, Global.G_city, note,
                                Global.G_imgUri, Global.G_day, Global.G_hour, Global.G_minutes, uid.toString(),carNumber
                            )
                            add_img(Global.G_imgUri)
                        }
                    }
                }
        }

        //ToDo Bottom Navigation Bar handling
        animatedBottomBar.onTabSelected = {
            when (it.id) {
                R.id.tab_add -> {
                    startActivity(Intent(this, add_car_page::class.java))
                    true
                }
                R.id.tab_home -> {
                    startActivity(Intent(this, home_page::class.java))
                    true
                }
                R.id.tab_my_auction -> {
                    startActivity(Intent(this, my_auction_page::class.java))
                    true
                }
                else -> false
            }
        }
        animatedBottomBar.onTabReselected = {
            when (it.id) {
                R.id.tab_add -> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_home -> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                R.id.tab_my_auction -> {
                    if (it.id == animatedBottomBar.selectedTab?.id)
                        nestedScrollView.smoothScrollTo(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    //ToDo function to add the car data into the firebase
    fun add_data(
        etcarBrand: String, etcarType: String, tvcarModel: String, etphoneNum: String,
        etstartinPrice: Int, spCities: String, tvNotes: String,
        pic: String, days: Int, hours: Int, minutes: Int, id:String,carNumber:String
    ) {
        val car = hashMapOf(
            "user_id" to uid?.email.toString(), "user" to "add", "brand" to etcarBrand, "type" to etcarType, "model" to tvcarModel,
            "contactnumber" to etphoneNum, "startingprice" to etstartinPrice, "city" to spCities, "Note" to tvNotes,
            "img" to pic, "day" to days, "hour" to hours, "minute" to minutes, "id" to id,"CarNumber" to carNumber,"currentPrice" to etstartinPrice
        )

        val carsadd = db.collection("cars")
        carsadd.add(car)
        val usercar = db.collection("users_add")
        usercar.add(car)
    }

    //ToDo function to add the car image into the firebase storage
    fun add_img(img: String) {
        var ref: StorageReference = FirebaseStorage.getInstance().getReference()
            .child("images/${img.toUri().lastPathSegment}")
        ref.putFile(img.toUri())
    }

    //ToDo function to check if the phone number is valid or not
    fun isValidPhoneNumber(phoneNumber: String) :Boolean{
        val pattern = Regex("^07\\d{8}\$")  // Regex pattern for '07' followed by 8 digits
        if ( pattern.matches(phoneNumber))
            return true
        else
            return false
    }

    //ToDo function to check if the Car number is valid or not
    fun isValidCarNumber(number: String): Boolean {
        val pattern = Regex("\\d{2}-\\d{5}") // Define the pattern using a regular expression

        if ( pattern.matches(number))
            return true
        else
            return false
    }



    fun check_carnumber(carNumberToCheck:String ,onSuccess: (Boolean) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val carsCollectionRef = db.collection("cars")
            carsCollectionRef
            .whereEqualTo("CarNumber", carNumberToCheck) // Query to find documents where CarNumber equals carNumberToCheck
            .get() // Execute the query
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onSuccess(false)
                } else {
                    onSuccess(true)
                }
            }
            .addOnFailureListener {
                // Handle any errors
                println("Error checking car number:")
            }
    }






    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, home_page::class.java))
    }

}

