package com.example.turborent.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.R
import com.example.turborent.activities.owner.OwnerDashboardActivity
import com.example.turborent.activities.renter.RenterDashboardActivity
import com.example.turborent.singeltonObject.FirebaseService
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var accountTypeRadioGroup: RadioGroup
    private lateinit var ownerRadioButton: RadioButton
    private lateinit var renterRadioButton: RadioButton
    private lateinit var registerButton: Button

    // Define db here
    private val db = FirebaseService.db

    //Define Firebase Auth
    private var auth = FirebaseService.auth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bindWidgets()

        wireEvents()

    }

    private fun wireEvents() {


        registerButton.setOnClickListener {

            registerUser()
        }

    }

    private fun bindWidgets() {
        // find views only – no logic yet
        firstNameEditText = findViewById(R.id.first_name_edittext)
        lastNameEditText = findViewById(R.id.last_name_edittext)
        emailEditText = findViewById(R.id.email_edittext)
        passwordEditText = findViewById(R.id.password_edittext)

        accountTypeRadioGroup = findViewById(R.id.account_type_radiogroup)
        ownerRadioButton = findViewById(R.id.owner_radiobutton)
        renterRadioButton = findViewById(R.id.renter_radiobutton)

        registerButton = findViewById(R.id.register_button)

        // click listeners / validation / Firebase will come later
    }

    private fun registerUser() {

        // validation

        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (password.isEmpty() || email.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            return
        }

        // checking if a button was selected
        val selectedId = accountTypeRadioGroup.checkedRadioButtonId

        if (selectedId == -1) return



        createUserAuth(email, password)

    }

    // creating the user profile in firestore
    private fun createUserFireStore(user: FirebaseUser?) {

        // fetching view values to store inside firebase

        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()

        // singling out the the selected radio button

        val selectedId = accountTypeRadioGroup.checkedRadioButtonId

        val selectedRadioButton = findViewById<RadioButton>(selectedId)

        val selectedRadioButtonText = selectedRadioButton.text.toString()


        // creating the user in firestore

        if (user != null) {


            // Add a new document with a generated id.
            val data = hashMapOf(

                "firstName" to firstName,
                "lastName" to lastName,
                "userType" to selectedRadioButtonText,
                "email" to email
            )

            db.collection("users")
                .document(user.uid)   // id = auth uid
                .set(data)
                .addOnSuccessListener {
                    // use the user type data once document is created in firestore

                    PostRegisterGoToScreen(selectedRadioButtonText)

                }
                .addOnFailureListener { e ->

                }


        }

    }

    // we register the user inside of the firebase auth
    private fun createUserAuth(email: String, password: String) {

        // registering the user inside auth

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // registered successfully
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    // on success of auth user is created and proper navigation is done to appropriate user type
                    createUserFireStore(user)


                    Toast.makeText(
                        baseContext,
                        "SUCCESS.",
                        Toast.LENGTH_SHORT,
                    ).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Log.e(TAG, "Registration failed: ${task.exception?.message}", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }

    }


    // Navigate to the right activity according to user type
    private fun PostRegisterGoToScreen(selectedUserType:String) {

        // Logic to navigate to Renter or Owner Dashboard

        if (selectedUserType == "Owner") {

            val intent = Intent(this, OwnerDashboardActivity::class.java)
            startActivity(intent)

        } else {

            val intent = Intent(this, RenterDashboardActivity::class.java)
            startActivity(intent)
             }

    }


    // activity ends here
}