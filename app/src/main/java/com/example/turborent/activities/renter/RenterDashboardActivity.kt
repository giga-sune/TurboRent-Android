package com.example.turborent.activities.renter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.LoginActivity
import com.example.turborent.R
import com.example.turborent.singeltonObject.FirebaseService.auth

class RenterDashboardActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var titleTextView: TextView
    private lateinit var browseCarsButton: Button
    private lateinit var myBookingsButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renter_dashboard)

        bindWidgets()
        wireEvents()
    }

    private fun bindWidgets() {
        titleTextView = findViewById(R.id.renter_dashboard_title_textview)
        browseCarsButton = findViewById(R.id.browse_cars_button)
        myBookingsButton = findViewById(R.id.my_bookings_button)
        logoutButton = findViewById(R.id.logout_button)
    }

    private fun wireEvents() {

        browseCarsButton.setOnClickListener {
            goToScreen(SearchCarsActivity::class)
        }

        myBookingsButton.setOnClickListener {
            goToScreen(MyBookingsActivity::class)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "You are signed out", Toast.LENGTH_SHORT).show()
            goToScreen(LoginActivity::class)
        }

    }


    fun goToScreen(activityClass: kotlin.reflect.KClass<out android.app.Activity>) {

        val intent = Intent(this, activityClass.java)
        startActivity(intent)

    }


    // activity ends here
}
