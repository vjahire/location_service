package com.vish.location.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vish.location.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyLocationService extends Service {
    private static final String TAG = MyLocationService.class.getSimpleName();
    private final int LOCATION_SERVICE_ID = 1;
    private final String channelId = "location_notification_channel";

    NotificationManager notificationManager;
    // initializing FusedLocationProviderClient
    FusedLocationProviderClient mFusedLocationClient;
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "onLocationResult: Latitude " + mLastLocation.getLatitude());
            Log.i(TAG, "onLocationResult: Longitude " + mLastLocation.getLongitude());
            Notification notification = createAndGetNotification();
            notificationManager.notify(LOCATION_SERVICE_ID, notification);
        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            Log.i(TAG, "onLocationAvailability: ");
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        Notification notification = createAndGetNotification();
        requestNewLocationData();
        startForeground(LOCATION_SERVICE_ID, notification);
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, Looper.myLooper());
    }

    private LocationRequest getLocationRequest() {
        // Initializing LocationRequest
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
//        mLocationRequest.setNumUpdates(1);
        return mLocationRequest;
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
    }

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private Notification createAndGetNotification() {
        Intent intentResult = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, intentResult, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        SimpleDateFormat dateFormat = new SimpleDateFormat("KK:mm:ss a", Locale.getDefault());
        String formattedTime = dateFormat.format(Calendar.getInstance().getTime());

        builder.setContentText("Running " + formattedTime);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);



        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }
}
