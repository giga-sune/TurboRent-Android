package com.example.turborent.activities.renter

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turborent.R
import com.example.turborent.adapters.RenterBookingsAdapter
import com.example.turborent.model.Booking
import com.example.turborent.singeltonObject.FirebaseService

class MyBookingsActivity : AppCompatActivity() {
    // --- VIEW PROPERTIES ---
    private lateinit var backButton: Button
    private lateinit var myBookingsTitleTextView: TextView
    private lateinit var myBookingsRecyclerView: RecyclerView

    private var allBookingsList = mutableListOf<Booking>()

    private lateinit var myBookingsAdapter: RenterBookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        bindWidgets()
        wireUpEvents()
        loadMyBookings()
    }

    private fun bindWidgets() {
        backButton = findViewById(R.id.back_button)

        myBookingsTitleTextView = findViewById(R.id.my_bookings_title_textview)

        myBookingsRecyclerView = findViewById(R.id.bookings_recycler_view)
    }

    private fun setupRecyclerView() {
        myBookingsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Pass the list and the delete function callback
        myBookingsAdapter = RenterBookingsAdapter(
            allBookingsList,
            { bookingId ->
                deleteBooking(bookingId)
            }
        )
        myBookingsRecyclerView.adapter = myBookingsAdapter
    }

    private fun wireUpEvents() {
        backButton.setOnClickListener {
            finish()
        }
    }


    private fun loadMyBookings(){
        val db = FirebaseService.db
        val loggedInUser = FirebaseService.auth.currentUser ?: return

        db.collection("bookings")
            .whereEqualTo("renterId", loggedInUser.uid) // Filter by the current RENTER ID
            .get()
            .addOnSuccessListener { snapshot ->
                allBookingsList.clear()

                for (doc in snapshot) {
                    // Deserialize the document into a Booking object
                    val booking = doc.toObject(Booking::class.java)

                    if (booking != null) {
                        // Ensure the Firestore document ID is captured in the model for deletion
                        val bookingWithId = booking.copy(id = doc.id)
                        allBookingsList.add(bookingWithId)
                    }
                }

                // Setup or update the RecyclerView with the loaded bookings
                setupRecyclerView()
            }
            .addOnFailureListener {

            }
    }

    private fun deleteBooking(bookingId: String) {
        val db = FirebaseService.db

        db.collection("bookings") // Correct collection name
            .document(bookingId)
            .delete()
            .addOnSuccessListener {
                // After successful deletion, reload the list to update the UI
                loadMyBookings()
            }
            .addOnFailureListener {
                // Handle deletion error
            }

    }


    override fun onResume() {
        super.onResume()
        loadMyBookings()
    }


}