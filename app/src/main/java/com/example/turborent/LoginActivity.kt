package com.example.turborent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.activities.RegisterActivity
import com.example.turborent.activities.owner.OwnerDashboardActivity
import com.example.turborent.activities.renter.RenterDashboardActivity
import com.example.turborent.singeltonObject.FirebaseService
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    // --- UI properties ---
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLinkTextView: TextView

    private lateinit var textViewErrorMessage: TextView




   // Define db here
    private val db = FirebaseService.db

    //Define Firebase Auth
    private var auth = FirebaseService.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindWidgets()

        wireEvents()



    }


    private fun bindWidgets(){
        // find views
        emailEditText = findViewById(R.id.email_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        loginButton = findViewById(R.id.login_button)
        registerLinkTextView = findViewById(R.id.register_link_textview)
        textViewErrorMessage = findViewById(R.id.textView_error_message)

    }
    private fun wireEvents() {


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            // TODO: validate + call Firebase Auth signInWithEmailAndPassword(email, password)

            signInAuth(email,password)

        }

        // Register link click → go to RegisterActivity
        registerLinkTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun PostLogInGoToScreen(document: com.google.firebase.firestore.DocumentSnapshot) {

        val TAG = "UserTypeCheck"
        //  Check if the document exists

        if (document.exists()) {
            // Safely cast the 'userType' field to String

            val userType = document.getString("userType")

           // Logic to navigate to Renter or Owner Dashboard

            if (userType == "Owner") {

                val intent = Intent(this, OwnerDashboardActivity::class.java)
                startActivity(intent)

            } else {

                val intent = Intent(this, RenterDashboardActivity::class.java)
                startActivity(intent)
            }
        } else {

        }
    }

    private fun currentUserFromFireStore(user: FirebaseUser?){

        if (user!=null) {
            val userId = user.uid

            val docRef = db.collection("users").document(user.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        PostLogInGoToScreen(document)

                    } else {

                    }
                }
                .addOnFailureListener { exception ->

                }

        }

    }


    private fun signInAuth(email:String,password:String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser

                    currentUserFromFireStore(user)


                } else {
                    // If sign in fails, display a message to the user.


                    textViewErrorMessage.visibility= View.VISIBLE

                }
            }

    }

    private fun updateUi() {

        passwordEditText.setText("")
        emailEditText.setText("")
        textViewErrorMessage.visibility= View.GONE

  }

    override fun onResume() {
        super.onResume()
        updateUi()
    }


// where activity ends

}