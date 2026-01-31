package com.example.turborent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turborent.R
import com.example.turborent.model.CarListing

class OwnerCarsAdapter(


    private var allListedCars: MutableList<CarListing>,

) : RecyclerView.Adapter<com.example.turborent.adapters.OwnerCarsAdapter.CarViewHolder>() {


    // ViewHolder holds references to the views/widgets in a single row (item_superhero.xml).

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val carImageView: ImageView =
            itemView.findViewById(R.id.car_imageview)

        val carTitleTextView: TextView =
            itemView.findViewById(R.id.car_title_textvieW)

        val licensePlateTextView: TextView =
            itemView.findViewById(R.id.license_plate_textview)

        val pricePerDayTextView: TextView =
            itemView.findViewById(R.id.price_per_day_textview)
    }


    /**
     * Called when RecyclerView needs a new row (ViewHolder).
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.car_item, parent, false)
        return CarViewHolder(view)
    }

    /**
     * Called to bind data to a ViewHolder at a given position.
     * This is where we set the text and image for each row.
     */

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {


        // Get the car at this position
        val car = allListedCars[position]



        // Bind the the car information to the item view
        holder.carTitleTextView.text = " ${car.brand} ${car.model}  "
        holder.licensePlateTextView.text = " ${car.licensePlate} "
        holder.pricePerDayTextView.text = " ${car.rentalCostPerDay} "

        
        // We are using Glide to load the car's image from the URL

        Glide.with(holder.itemView.context)
            .load(car.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground) // this is a fallback image if URL is empty/broken
            .into(holder.carImageView)
        


    }

    //
    override fun getItemCount(): Int = allListedCars.size


}