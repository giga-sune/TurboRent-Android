package com.example.turborent.activities.renter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.turborent.R
import com.example.turborent.model.CarListing
import com.example.turborent.model.UserProfile
import com.example.turborent.singeltonObject.FirebaseService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class BookingCarDetailActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var carImageView: ImageView
    private lateinit var carNameTextView: TextView
    private lateinit var carColorTextView: TextView
    private lateinit var carPlateTextView: TextView
    private lateinit var carOwnerTextView: TextView
    private lateinit var carPriceTextView: TextView
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText

    private lateinit var textViewErrorMessage: TextView
    private lateinit var StartTextViewErrorMessage: TextView
    private lateinit var EndTextViewErrorMessage: TextView

    private lateinit var bookNowButton: Button

    private lateinit var backButton: Button



    // --- Firebase + data ---
    private val db = FirebaseService.db

    private val auth = FirebaseService.auth

    // used to fill in owner details in ui



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_car_detail)


        bindWidgets()


        wireEvents()

        loadCar()


    }

    private fun bindWidgets() {
        carImageView = findViewById(R.id.car_imageview)
        carNameTextView = findViewById(R.id.car_name_textview)
        carColorTextView = findViewById(R.id.car_color_textview)
        carPlateTextView = findViewById(R.id.car_plate_textview)
        carOwnerTextView = findViewById(R.id.car_owner_textview)
        carPriceTextView = findViewById(R.id.car_price_textview)
        startDateEditText = findViewById(R.id.start_date_edittext)
        endDateEditText = findViewById(R.id.end_date_edittext)
        bookNowButton = findViewById(R.id.book_now_button)
        textViewErrorMessage = findViewById(R.id.textView_error_message)
        StartTextViewErrorMessage = findViewById(R.id.start_textView_error_message)
        EndTextViewErrorMessage = findViewById(R.id.end_textView_error_message)
        backButton = findViewById(R.id. back_button)

        updateUiclearing()

    }

    private fun loadIdIntent(): String?{

        return  intent.getStringExtra("car_doc_ID")

    }
    private fun loadCar() {

        // Get the ID and check for null
        val docId = loadIdIntent()
        if (docId.isNullOrEmpty()) {
            Toast.makeText(this, "Car ID missing.", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        val docRef = db.collection("carListings").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // deserealize firestore document in to a car object, and extrat owner id to find owner
                    val car = document.toObject(CarListing::class.java)
                    if (car!=null) {
                        val ownerId = car.ownerId
                        // load the owner of the car, and upon successful loading update ui with details of car and owner
                        loadOwner(ownerId,car)
                    }

                } else {


                }
            }
            .addOnFailureListener { exception ->
            }

    }

    // to show car details
    private fun updateUi(car: CarListing,ownerUser: UserProfile) {

        carNameTextView.text = "${car.brand} ${car.model}"
        carColorTextView.text = "Color: ${car.color}"
        carPlateTextView.text = "Plate: ${car.licensePlate}"
        carOwnerTextView.text = "Owner: ${ownerUser.firstName} ${ownerUser.lastName}"   // depends on how you store it
        carPriceTextView.text = "Cost per day: $${car.rentalCostPerDay}"

        // image with Glide
        Glide.with(this)
            .load(car.photoUrl)
            .placeholder(R.color.turborent_input_bg)  // fallback bg color
            .error(R.color.turborent_input_bg)        // if url is bad
            .centerCrop()
            .into(carImageView)

    }

    private fun wireEvents() {
        bookNowButton.setOnClickListener {


            loadCarForBooking()
        }

        backButton.setOnClickListener {
            finish()

        }

    }

    // Firestore loading

    // Parse "YYYY-MM-DD" → Long (millis)
    // return null if invalid
    private fun parseDateToMillis(input: String): Long? {
        if (input.isBlank()) return null

        // strict parser
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
        sdf.isLenient = false

        return try {
            val date = sdf.parse(input)
            date?.time   // Long millis
        } catch (e: ParseException) {
            null
        }
    }

    private fun createBooking(ownerId: String, car: CarListing) {

        // validation check

        //  Read + trim inputs
        val startText = startDateEditText.text.toString().trim()
        val endText   = endDateEditText.text.toString().trim()

        // Basic empty check
        if (startText.isEmpty() || endText.isEmpty()) {
            startDateEditText.error = if (startText.isEmpty()) "Required" else null
            endDateEditText.error   = if (endText.isEmpty()) "Required" else null
            return
        }

        //  Parse to millis
        val startMillis = parseDateToMillis(startText)
        val endMillis   = parseDateToMillis(endText)

        if (startMillis == null) {

            StartTextViewErrorMessage.text= "Use format YYYY-MM-DD"
            StartTextViewErrorMessage.visibility= View.VISIBLE


            return
        }

        if (endMillis == null) {

            EndTextViewErrorMessage.text = "Use format YYYY-MM-DD"
            EndTextViewErrorMessage.visibility= View.VISIBLE

            return
        }

        //  Logical check: end after start
        if (endMillis < startMillis) {
            EndTextViewErrorMessage.text = "End date must be after start date"
            EndTextViewErrorMessage.visibility= View.VISIBLE

            return
        }

        // Renter must be logged in
        val renterId = auth.currentUser?.uid
        if (renterId == null) {

            return
        }

        // Build booking data in firestore
        val data = hashMapOf(
            "carlistingId"    to car.Docid,
            "carPhoto"        to car.photoUrl,
            "ownerId"         to ownerId,
            "renterId"        to renterId,
            "carAddress"      to car.address,
            "carBrand"        to car.brand,
            "carModel"        to car.model,
            "carColor"        to car.color,
            "carLicensePlate" to car.licensePlate,
            "pricePerDay"     to car.rentalCostPerDay,
            "startDate"       to startMillis,
            "endDate"         to endMillis
        )

        //  Write to Firestore
        db.collection("bookings")
            .add(data)
            .addOnSuccessListener { docRef ->
                // create confirmation code
                val docId = docRef.id
                val confirmationCode = docId.take(8).uppercase()  // simple example

                docRef.update(
                    mapOf(
                        "id" to docId,
                        "confirmationCode" to confirmationCode
                    )
                )


                textViewErrorMessage.visibility= View.VISIBLE
                textViewErrorMessage.text = "Booking created"


            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create booking.", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadCarForBooking() {

        // Get the ID and check for null
        val docId = loadIdIntent()
        if (docId.isNullOrEmpty()) {

            finish()
            return
        }

        val docRef = db.collection("carListings").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // deserealize firestore document in to a car object, and extrat owner id to find owner
                    val car = document.toObject(CarListing::class.java)
                    if (car!=null) {
                        val ownerId = car.ownerId
                        // create booking upon successfully loading the booked car
                        createBooking(ownerId,car)
                    }

                } else {

                }

            }
            .addOnFailureListener { exception ->
            }

    }

    private fun loadOwner(ownerId:String,car: CarListing){

        val docRef = db.collection("users").document(ownerId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val owner = document.toObject(UserProfile::class.java)
                    if (owner!=null) {
                        // get the owner object for chosen car
                        val ownerUser= owner
                        updateUi(car,ownerUser)

                    }

            } else {

                }
            }
            .addOnFailureListener { exception ->

            }

    }

    // To refresh errors
    private fun updateUiclearing() {

        endDateEditText.setText("")
        startDateEditText.setText("")
        textViewErrorMessage.visibility = View.GONE
        EndTextViewErrorMessage.visibility = View.GONE
        StartTextViewErrorMessage.visibility = View.GONE

    }


    // activity ends here
}