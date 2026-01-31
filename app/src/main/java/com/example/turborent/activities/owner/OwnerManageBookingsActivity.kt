package com.example.turborent.activities.owner

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turborent.R
import com.example.turborent.adapters.OwnerBookingsAdapter
import com.example.turborent.model.Booking
import com.example.turborent.singeltonObject.FirebaseService

class OwnerManageBookingsActivity : AppCompatActivity() {
    // --- VIEW PROPERTIES ---
    private lateinit var backButton: Button
    private lateinit var manageBookingsTitleTextView: TextView
    private lateinit var ownerBookingsRecyclerView: RecyclerView

    private var allBookingsList = mutableListOf<Booking>()

    private lateinit var ownerBookingsAdapter: OwnerBookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_manage_bookings)

        bindWidgets()
        wireUpEvents()
    }

    override fun onResume() {
        super.onResume()
        loadOwnerBookings()
    }

    private fun bindWidgets() {
        backButton = findViewById(R.id.back_button)
        manageBookingsTitleTextView = findViewById(R.id.manage_bookings_title_textview)
        ownerBookingsRecyclerView = findViewById(R.id.owner_bookings_recyclerview)
    }

    private fun setupRecyclerView() {
        ownerBookingsRecyclerView.layoutManager = LinearLayoutManager(this)

        ownerBookingsAdapter = OwnerBookingsAdapter(
            allBookingsList,
            { bookingId ->
                deleteBooking(bookingId)
            }
        )
        ownerBookingsRecyclerView.adapter = ownerBookingsAdapter
    }

    private fun wireUpEvents() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadOwnerBookings() {
        val db = FirebaseService.db
        val loggedInUser = FirebaseService.auth.currentUser ?: return

        //  Query all bookings for the current owner

        db.collection("bookings")
            .whereEqualTo("ownerId", loggedInUser.uid)
            .get()
            .addOnSuccessListener { bookingSnapshot ->

                allBookingsList.clear()

                val pendingBookings = mutableListOf<Booking>()
                var lookupsRemaining = bookingSnapshot.size()

                if (lookupsRemaining == 0) {
                    setupRecyclerView()
                    return@addOnSuccessListener
                }

                for (bookingDoc in bookingSnapshot) {
                    val booking = bookingDoc.toObject(Booking::class.java)

                    if (booking != null) {
                        val renterId = booking.renterId


                        // use the renterId to directly access the user document in the "users" collection
                        db.collection("users").document(renterId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                // Combine firstName and lastName
                                val firstName = userDoc.getString("firstName") ?: ""
                                val lastName = userDoc.getString("lastName") ?: ""
                                val renterName = "$firstName $lastName".trim()

                                //  Create the final Booking object with the Renter Name
                                val bookingWithIdAndName = booking.copy(
                                    id = bookingDoc.id,
                                    renterName = renterName // Update the Booking model property
                                )
                                pendingBookings.add(bookingWithIdAndName)

                                //  Check if all asynchronous lookups are complete
                                lookupsRemaining--
                                if (lookupsRemaining == 0) {
                                    allBookingsList.addAll(pendingBookings)
                                    // Setup/update the adapter after all data is ready
                                    setupRecyclerView()
                                }
                            }
                            .addOnFailureListener {
                                // If user lookup fails, use a default name
                                val bookingWithIdAndName = booking.copy(
                                    id = bookingDoc.id,
                                    renterName = "Renter (Details Unavailable)"
                                )
                                pendingBookings.add(bookingWithIdAndName)

                                lookupsRemaining--
                                if (lookupsRemaining == 0) {
                                    allBookingsList.addAll(pendingBookings)
                                    setupRecyclerView()
                                }
                            }
                    } else {
                        lookupsRemaining--
                        if (lookupsRemaining == 0) {
                            allBookingsList.addAll(pendingBookings)
                            setupRecyclerView()
                        }
                    }
                }
            }
            .addOnFailureListener {

            }

    }

    private fun deleteBooking(bookingId: String) {
        val db = FirebaseService.db

        db.collection("bookings")
            .document(bookingId)
            .delete()
            .addOnSuccessListener {
                loadOwnerBookings()
            }
            .addOnFailureListener {

            }
    }



}