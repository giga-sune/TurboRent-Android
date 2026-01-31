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


class OwnerBookingsAdapter (
    // List of bookings to display
    private var allBookings: MutableList<Booking>,

    private val onCancelBookingClicked: (String) -> Unit,

    ) : RecyclerView.Adapter<OwnerBookingsAdapter.BookingViewHolder>() {


    // ViewHolder holds references to the views/widgets in a single row (using the owner booking item layout).
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Views specific to the Owner Item XML
        val renterNameTextView: TextView =
            itemView.findViewById(R.id.renter_name_textview)

        val confirmationCodeTextView: TextView =
            itemView.findViewById(R.id.confirmation_code_textview)

        val pricePerDayTextView: TextView =
            itemView.findViewById(R.id.price_per_day_textview)

        val licensePlateTextView: TextView =
            itemView.findViewById(R.id.license_plate_textview)

        val dateRangeTextView: TextView =
            itemView.findViewById(R.id.date_range_textview)

        val cancelButton: Button =
            itemView.findViewById(R.id.cancel_booking_button)

        val carImageView: ImageView =
            itemView.findViewById(R.id.car_imageview)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        // Inflate the owner-specific booking item layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.owner_booking_item, parent, false) // Assuming R.layout.item_owner_booking based on context
        return BookingViewHolder(view)
    }

    /**
     * bind data to a ViewHolder at a given position.
     */
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        // Get the booking at this position
        val booking = allBookings[position]

        // to format timestamps to a readable date string
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        // --- Bind the Booking data to the item view (Owner-specific details) ---

        holder.renterNameTextView.text = booking.renterName // Assuming 'renterName' is added/loaded

        holder.confirmationCodeTextView.text = "Code: ${booking.confirmationCode}"

        // Price, License Plate, and Dates are from the Booking model
        holder.pricePerDayTextView.text = "$${String.format("%.2f", booking.pricePerDay)}/day"
        holder.licensePlateTextView.text = "Plate: ${booking.carLicensePlate}"


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
            // Trigger the delete callback, passing the booking document ID
            onCancelBookingClicked(booking.id)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = allBookings.size


}