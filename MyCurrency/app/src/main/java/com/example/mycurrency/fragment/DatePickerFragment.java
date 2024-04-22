package com.example.mycurrency.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.mycurrency.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    int year, month, day;
    final Calendar c = Calendar.getInstance();
    String date;

    public DatePickerFragment(){
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    // Restituzione dialogo del picker della data
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long week_seconds = 7 * 24 * 3600;
        long week_milliS = week_seconds * 1000;
        new DatePickerFragment();

        // Crea una nuova istanza di DatePickerDialog
        DatePickerDialog pickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);
        pickerDialog.getDatePicker().setMinDate(c.getTime().getTime() - week_milliS );
        pickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());
        return pickerDialog;
    }

    // Selezione data
    public void onDateSet(DatePicker view, int year, int month, int day) {
        date = year + "/" + (month + 1) + "/" + day;
        TextView picked_date = (TextView) requireActivity().findViewById(R.id.picked_date_text);
        picked_date.setText(getDate());
    }

    public String getDate(){
        return date;
    }
}
