# Ride Share Android App (Turo Clone)

A native Android application built with Kotlin and Android Studio as part of an Advanced Android course project.  
This app is a simplified clone of Turo, allowing car owners to list vehicles for rent and renters to search, book, and manage car rentals.

## Overview

The app supports two distinct user roles:

- **Car Owners** – list cars, view their listings, and manage bookings
- **Renters** – search for cars by city, view listings on a map, and book rentals

The UI and available screens change based on the logged-in user role.

## Key Features

### Authentication & User Roles
- Firebase Authentication (email & password)
- User profiles stored in Firestore
- Clear separation between Owner and Renter flows
- Logout functionality

### Owner Features
- Create car listings
- View all owned car listings
- Manage bookings made by renters
- Cancel bookings

### Renter Features
- Search available cars by city
- View listings on a map with price markers
- View car details and booking screen
- Book cars for a selected date range
- View and cancel personal bookings

## Screens Included
- Login
- Register
- Owner Dashboard (Create Listing, My Listings, Manage Bookings)
- Renter Dashboard (Search, Car Profile, My Bookings)

## Tech Stack
- **Language:** Kotlin
- **UI:** XML Layouts (LinearLayouts)
- **Architecture:** OOP with separation of UI and business logic
- **Backend:** Firebase Firestore
- **Authentication:** Firebase Auth
- **Maps:** Google Maps API

## Notes
This project was built under academic time constraints with a focus on core functionality, app structure, and understanding of Android fundamentals.  
Areas such as error handling and UI polish are planned for further improvement.

## Status
Completed as a course project. Actively being refined and improved.

---

Built as part of the **Advanced Android** course.
