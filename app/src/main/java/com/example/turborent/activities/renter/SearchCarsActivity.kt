package com.example.turborent.activities.renter

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turborent.R
import com.example.turborent.model.CarListing
import com.example.turborent.singeltonObject.FirebaseService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SearchCarsActivity : AppCompatActivity() {




    private lateinit var searchButton: Button
    private lateinit var searchCityEdittext: EditText

    private lateinit var backButton: Button

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var markerMap: GoogleMap

    private val db= FirebaseService.db

    private val allCars = mutableListOf<CarListing>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_cars)

        bindWidgets()

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment

        mapFragment.getMapAsync { map ->

            makeMap(map)

        }

        wireEvents()

        }



    private fun bindWidgets() {

        searchButton = findViewById(R.id.search_button)
        searchCityEdittext = findViewById(R.id.search_city_edittext)
        backButton = findViewById(R.id. back_button)

    }

    private fun loadAllCars(){

        db.collection("carListings")
            .get()
            .addOnSuccessListener { result ->
                allCars.clear()

                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")

       val car = document.toObject(CarListing::class.java)
//
//                    val carRight = CarListing(
//                        Docid=document.id,
//                        ownerId = document.getString("ownerId")?:"",
//                        brand = document.getString("brand")?:"",
//                        model = document.getString("model")?:"",
//                        color = document.getString("color")?:"",
//                        licensePlate = document.getString("licensePlate")?:"",
//                        rentalCostPerDay = document.getDouble("rentalCostPerDay")?:0.0,
//                        city = document.getString("city")?:"",
//                        address = document.getString("address")?:"",
//                        photoUrl = document.getString("photoUrl")?:"",
//
//                    )


                    if(car!=null){
                        val carWithId = car.copy(Docid = document.id)
                        allCars.add(carWithId)
                    }

                }
                // create markers for cars loaded from firestore on successs
                showListedCars()

            }
            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
            }


    }
     private fun makeMap(map:GoogleMap) {

    markerMap=map

    map.uiSettings.isZoomControlsEnabled= true

    // load cars from firestore and make markers for each car on map
    loadAllCars()

    // event listner to repostion map and show pop up info about marker
    markerMap.setOnMarkerClickListener{ marker ->


        val lat = marker.position.latitude
        val long = marker.position.longitude
        // reposition map
        markerMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(lat , long ), 13f)
        )


        marker.showInfoWindow()

        true
    }

    // event listner that would take us to the details page of the marked car
    markerMap.setOnInfoWindowClickListener { marker->

        // pull doc id from the marker
        val markedCarDocId = marker.tag as? String
        if (markedCarDocId == null) {
            // if no id return safely
            return@setOnInfoWindowClickListener
        }
        // moving to the details screen upon click of the marker
        val intent = Intent(this, BookingCarDetailActivity::class.java)
        intent.putExtra("car_doc_ID",markedCarDocId)
        startActivity(intent)

    }

    }

    // this will be inside the loadallcars function, because it can only work on success of data loading

    private fun showListedCars(){

        // making sure that we have a map before attempting to create markers on it

        if (!::markerMap.isInitialized) return

        for (item in allCars) {

            // obtaining the longitude and latitude values from the location input, to pass into marker

            val geoPostion = findLocation(item.address)

            if (geoPostion != null) {

                val  rentalPrice = item.rentalCostPerDay.toString()

                val marker= markerMap.addMarker(
                    MarkerOptions()
                        .position(geoPostion)
                        .title("${rentalPrice}$")
                        .snippet(item.brand)
                )

                marker?.tag = item.Docid

            }


        }

        // positioning the map
        markerMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(43.65, -79.38), 13f)
        )

    }

    private fun searchCity() {
        val city = searchCityEdittext.text.toString().trim()
        if (city.isEmpty()) {

            // in layout message
            Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show()
            return

        }


            // we do this to obtain the long and lat of the location, later to be used in animate to position us there.
            val geo = Geocoder(this)
            val list = geo.getFromLocationName(city, 1)

            if (list.isNullOrEmpty()) {

                // in layout message
                Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show()
                return
            }

            val loc = list.first()
            val pos = LatLng(loc.latitude, loc.longitude)

            markerMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f))


    }

    private fun wireEvents(){

        backButton.setOnClickListener {
            finish()

        }

        searchButton.setOnClickListener {
            searchCity()
        }
    }
    private fun findLocation(location:String):LatLng?{


        val geo = Geocoder(this)
        val list = geo.getFromLocationName(location, 1)

        if (list.isNullOrEmpty()) {
            return null
        }

        val loc = list.first()
        val pos = LatLng(loc.latitude, loc.longitude)

        return pos

    }


    // activity ends here
}
