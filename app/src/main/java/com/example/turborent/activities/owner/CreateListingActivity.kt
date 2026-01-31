package com.example.turborent.activities.owner

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.R
import com.example.turborent.singeltonObject.FirebaseService

class CreateListingActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var brandEditText: EditText
    private lateinit var modelEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var licensePlateEditText: EditText
    private lateinit var rentalCostEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var photoUrlEditText: EditText

    private lateinit var backButton: Button
    private lateinit var createButton: Button

    // Define db here
    private val db = FirebaseService.db

    //Define Firebase Auth
    private var auth = FirebaseService.auth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_listing)

        bindWidgets()
        wireEvents()
    }

    private fun bindWidgets() {
        brandEditText = findViewById(R.id.brand_edittext)
        modelEditText = findViewById(R.id.model_edittext)
        colorEditText = findViewById(R.id.color_edittext)
        licensePlateEditText = findViewById(R.id.license_plate_edittext)
        rentalCostEditText = findViewById(R.id.rental_cost_edittext)
        cityEditText = findViewById(R.id.city_edittext)
        addressEditText = findViewById(R.id.address_edittext)
        photoUrlEditText = findViewById(R.id.photo_url_edittext)

        backButton = findViewById(R.id.back_button)
        createButton = findViewById(R.id.create_button)
    }

    private fun wireEvents() {
        // Back → simply go to previous screen
        backButton.setOnClickListener {
            finish()
        }

        // Create → you’ll plug in validation + Firestore logic here
        createButton.setOnClickListener {

            // validation
            val brandEditTextString =  brandEditText.text.toString()
            val modelEditTextString =  modelEditText.text.toString()
            val licensePlateEditTextString =  licensePlateEditText.text.toString()
            val photoUrlEditTextString =  photoUrlEditText.text.toString()
            val addressEditTextString =  addressEditText.text.toString()
            val cityEditTextString =  cityEditText.text.toString()
            val rentalCostEditTextString =  rentalCostEditText.text.toString()
            val colorEditTextString =  colorEditText.text.toString()

            if(brandEditTextString.isEmpty()||rentalCostEditTextString.isEmpty()||cityEditTextString.isEmpty()
                ||addressEditTextString.isEmpty()||photoUrlEditTextString.isEmpty()||licensePlateEditTextString.isEmpty()
                ||modelEditTextString.isEmpty()
                )
              {
                Toast.makeText(this,"the fields can not be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
              }

            // create car listing in firestore


            val LoggedInUser = auth.currentUser

            // Add a new document with a generated id.
            val data = hashMapOf(
                "ownerId" to LoggedInUser?.uid,
                "brand" to brandEditTextString,
                "model" to modelEditTextString,
                "licensePlate" to licensePlateEditTextString,
                "color" to colorEditTextString,
                "city" to cityEditTextString,
                "address" to addressEditTextString,
                "photoUrl" to photoUrlEditTextString,
                "rentalCostPerDay" to rentalCostEditTextString.toDouble()
            )

            db.collection("carListings")
                .add(data)
                .addOnSuccessListener { documentReference ->

                }
                .addOnFailureListener { e ->

                }

        }
    }
}
