package se.berellstudios.app.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

fun showDateTimePicker(
    context: Context,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    //Show DatePickerDialog
    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)

            //After selecting a date, show TimePickerDialog
            TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    val selectedTime = LocalTime.of(selectedHour, selectedMinute)

                    //Combine date and time into LocalDateTime and pass it to the callback
                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                    onDateTimeSelected(dateTime)
                },
                hour, minute, true
            ).show()
        },
        year, month, day
    ).show()
}
