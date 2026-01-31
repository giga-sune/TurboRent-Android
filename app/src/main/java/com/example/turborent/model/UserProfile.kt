package com.example.turborent.model

data class UserProfile(

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val userType: String = "",        // "owner" or "renter"
)
