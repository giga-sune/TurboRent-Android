package com.example.turborent.model

data class Booking(val id: String = "",
                   val confirmationCode: String = "",
                   val carPhoto: String ="",
                   val ownerId: String = "",
                   val renterId: String = "",
                   val renterName: String = "",
                   val carBrand: String = "",
                   val carModel: String = "",
                   val carColor: String = "",
                   val carLicensePlate: String = "",
                   val carAddress: String = "",
                   val pricePerDay: Double = 0.0,
                   val startDate: Long = 0L,                  // millisec
                   val endDate: Long = 0L )
