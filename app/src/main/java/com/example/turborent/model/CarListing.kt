package com.example.turborent.model

data class CarListing(    val Docid: String = "",              // Firestore doc id
                          val ownerId: String = "",         // link to UserProfile.id / Auth uid
                          val brand: String = "",
                          val model: String = "",
                          val color: String = "",
                          val licensePlate: String = "",
                          val rentalCostPerDay: Double = 0.0,
                          val city: String = "",
                          val address: String = "",
                          val photoUrl: String = "",
                          )

