package com.example.app_scheduler.ui.utility

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class DatePickerFragment(private val listener: DateTimePickerListener) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        var timeInMillis = arguments?.getLong(Utility.TAG_DATE_PICKER,System.currentTimeMillis())
        val c = Calendar.getInstance()
        timeInMillis?.let {
            c.time = Date(it)
        }
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val monthOfYear = month+1
        val date = "$day-$monthOfYear-$year"
        listener.onPick(date)
    }
}