package com.example.turborent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turborent.R
import com.example.turborent.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RenterBookingsAdapter(
    // List of bookings to display
    private var allBookings: MutableList<Booking>,


    private val onCancelBookingClicked: (String) -> Unit,

    ) : RecyclerView.Adapter<RenterBookingsAdapter.BookingViewHolder>() {


    // ViewHolder holds references to the views/widgets in a single row (using the booking item layout).
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val confirmationCodeTextView: TextView =
            itemView.findViewById(R.id.confirmation_code_textview)

        val carTitleTextView: TextView =
            itemView.findViewById(R.id.car_title_textview)

        val carColorTextView: TextView =
            itemView.findViewById(R.id.car_color_textview)

        val carAddressTextView: TextView =
            itemView.findViewById(R.id.car_address_textview)

        val dateRangeTextView: TextView =
            itemView.findViewById(R.id.date_range_textview)

        val cancelButton: Button =
            itemView.findViewById(R.id.cancel_booking_button)

        val carImageView: ImageView =
            itemView.findViewById(R.id.car_imageview)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.renter_booking_item, parent, false) // <-- Using R.layout.booking_item
        return BookingViewHolder(view)
    }

    /**
     *  bind data to a ViewHolder at a given position.
     */
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        // Get the booking at this position
        val booking = allBookings[position]

        // Helper to format timestamps to a readable date string
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        // --- Bind the Booking data to the item view ---

        // Top Info
        holder.confirmationCodeTextView.text = booking.confirmationCode
        holder.carTitleTextView.text = "${booking.carBrand} ${booking.carModel}"
        holder.carColorTextView.text = "Color: ${booking.carColor}"
        holder.carAddressTextView.text = booking.carAddress

        // Dates
        val startDate = dateFormat.format(Date(booking.startDate))
        val endDate = dateFormat.format(Date(booking.endDate))
        holder.dateRangeTextView.text = "$startDate - $endDate"

        // glide
        Glide.with(holder.itemView.context)
            .load(booking.carPhoto)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.carImageView)

        // Wire the Cancel Button Event

        holder.cancelButton.setOnClickListener {
            // Trigger the delete
            onCancelBookingClicked(booking.id)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = allBookings.size


}