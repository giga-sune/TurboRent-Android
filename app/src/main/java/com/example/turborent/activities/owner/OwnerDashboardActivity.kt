package com.example.turborent.activities.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.LoginActivity
import com.example.turborent.R
import com.example.turborent.singeltonObject.FirebaseService.auth

class OwnerDashboardActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var titleTextView: TextView
    private lateinit var addCarButton: Button
    private lateinit var myCarsButton: Button
    private lateinit var manageBookingsButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dashboard)

        bindWidgets()
        wireEvents()
    }

    private fun bindWidgets() {
        titleTextView = findViewById(R.id.owner_dashboard_title_textview)
        addCarButton = findViewById(R.id.add_car_button)
        myCarsButton = findViewById(R.id.my_cars_button)
        manageBookingsButton = findViewById(R.id.manage_bookings_button)
        logoutButton = findViewById(R.id.logout_button)
    }

    private fun wireEvents() {
        addCarButton.setOnClickListener {

            goToScreen(CreateListingActivity::class)
        }

        myCarsButton.setOnClickListener {

            goToScreen(MyCarsActivity::class)
        }

        manageBookingsButton.setOnClickListener {

            goToScreen(OwnerManageBookingsActivity::class)
        }

        logoutButton.setOnClickListener {
            // logout and navigation
            auth.signOut()
            Toast.makeText(this,"you are signed out",Toast.LENGTH_SHORT).show()
            goToScreen(LoginActivity::class)
        }
    }

    fun goToScreen(activityClass: kotlin.reflect.KClass<out android.app.Activity>) {

        val intent = Intent(this, activityClass.java)
        startActivity(intent)
    }



    // activity ends here
}
