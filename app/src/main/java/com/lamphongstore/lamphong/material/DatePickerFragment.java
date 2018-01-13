package com.lamphongstore.lamphong.material;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Norvia on 20/03/2017.
 */

public class DatePickerFragment extends DialogFragment {
    private int year;
    private int month;
    private int day;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (year == 0) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),
                year, month, day);
        dialog.setTitle("Set a Date");
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;
    }

    public void setSelectedDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
