package com.example.turborent.activities.owner

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turborent.R
import com.example.turborent.adapters.OwnerCarsAdapter
import com.example.turborent.model.CarListing
import com.example.turborent.singeltonObject.FirebaseService

class MyCarsActivity : AppCompatActivity() {
    // --- VIEW PROPERTIES ---
    private lateinit var backButton: Button
    private lateinit var myCarsTitleTextView: TextView
    private lateinit var myCarsRecyclerView: RecyclerView

    private var carsList = mutableListOf<CarListing>()



    private lateinit var myCarsAdapter: OwnerCarsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cars)

        bindWidgets()
        wireUpEvents()
        loadMyCars()
    }

    private fun bindWidgets() {

        backButton = findViewById(R.id.back_button)
        myCarsTitleTextView = findViewById(R.id.my_cars_title_textview)
        myCarsRecyclerView = findViewById(R.id.my_cars_recyclerview)

    }

    private fun setupRecyclerView() {

        myCarsRecyclerView.layoutManager = LinearLayoutManager(this)
        myCarsAdapter = OwnerCarsAdapter(carsList)
        myCarsRecyclerView.adapter = myCarsAdapter

    }

    private fun wireUpEvents() {

        backButton.setOnClickListener {
            finish()  // just go back to previous screen
        }

    }

    private fun loadMyCars(){

       val db = FirebaseService.db

       val LoggedInUser = FirebaseService.auth.currentUser?: return

        db.collection("carListings")
            .whereEqualTo("ownerId", LoggedInUser.uid)
            .get()
            .addOnSuccessListener { snapshot ->

                carsList.clear()   // reset old data

                for (doc in snapshot) {
                    val car = doc.toObject(CarListing::class.java)

                    if (car != null) {
                        val carWithId = car.copy(Docid = doc.id)
                        carsList.add(carWithId)
                    }
                }

                // now `cars` has all this owner’s listings
                setupRecyclerView()
            }
            .addOnFailureListener { e ->
                // handle error
            }


    }



    // activity ends here
    }
