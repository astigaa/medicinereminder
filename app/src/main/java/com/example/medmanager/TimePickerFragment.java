package com.example.medmanager;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Saat seçiciyi gösteren bir dialog fragment
public class TimePickerFragment extends DialogFragment {

    // Dialog oluşturulduğunda çağrılan metot
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Mevcut tarih ve saat bilgisini al
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY); // Saat bilgisini al
        int min = c.get(Calendar.MINUTE);       // Dakika bilgisini al

        // Oluşturulan TimePickerDialog'u döndür
        // getActivity() metodu, fragment'ın bağlı olduğu Activity'i verir
        // OnTimeSetListener, saatin seçildiğinde çağrılacak olan listener'ı temsil eder
        // DateFormat.is24HourFormat(getActivity()), 24 saat formatının kullanılıp kullanılmadığını belirler
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, min, DateFormat.is24HourFormat(getActivity()));
    }
}
