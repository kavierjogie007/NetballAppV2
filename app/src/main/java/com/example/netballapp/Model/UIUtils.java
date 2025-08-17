package com.example.netballapp.Model;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class UIUtils {

    // Opens a calendar and sets selected date into the provided EditText
    public static void showDatePicker(Context context, EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        // Try to parse existing date in EditText
        String currentDate = targetEditText.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1; // Month is 0-based
                int day = Integer.parseInt(parts[2]);
                calendar.set(year, month, day);
            } catch (Exception e) {
                // ignore parse errors and use current date
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    targetEditText.setText(date);
                }, year, month, day);

        // Prevent selecting future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showFutureDatePicker(Context context, EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        // Try to parse existing date
        String currentDate = targetEditText.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1; // Month is 0-based
                int day = Integer.parseInt(parts[2]);
                calendar.set(year, month, day);
            } catch (Exception e) {
                // ignore parse errors
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    targetEditText.setText(date);
                }, year, month, day);

        // Only allow future dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }


}
