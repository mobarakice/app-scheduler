package com.example.app_scheduler.ui.utility

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class TimePickerFragment (private val listener: DateTimePickerListener): DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val time = arguments?.getLong(Utility.TAG_TIME_PICKER,System.currentTimeMillis())
        val c = Calendar.getInstance()
        time?.let {
            c.time = Date(it)
        }
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        listener.onPick("$hourOfDay:$minute")
    }
}