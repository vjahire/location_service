package com.vish.location.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyBootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BOOT COMPLETE", Toast.LENGTH_LONG).show();


        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("boot_logs", MODE_PRIVATE);

        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext

        SimpleDateFormat dateFormat = new SimpleDateFormat("KK_mm_ss_a", Locale.getDefault());
        String formattedTime = dateFormat.format(Calendar.getInstance().getTime());

        myEdit.putString(formattedTime, intent.getAction());

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyLocationService.class));
        } else
            context.startService(new Intent(context, MyLocationService.class));
    }
}
