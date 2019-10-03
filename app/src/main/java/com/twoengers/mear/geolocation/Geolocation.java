package com.twoengers.mear.geolocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/** Класс для определения местоположения. */
public class Geolocation {
    /** Контекст, в котором создан объект класса. */
    private Context context;
    /** Коллбэк, вызываемый для передачи данных о местоположении в активность. */
    private GeolocationCallback geolocationCallBack;
    /** Объект для взаимодействия с провайдером геолокации. */
    private FusedLocationProviderClient fusedLocationProviderClient;
    /** Константа с индексом разрешения для доступа к геолокации. */
    public final int REQUEST_LOCATION_PERMISSION = 1;

    /**
     * Конструктор класса Geolocation.
     * Входные параметры:
     *      (Context) context - контекст, в котором необходимо определить местоположение;
     *      (GeolocationCallback) geolocationCallback - коллбэк, вызываемый при получении данных
     *      о местоположении.
     */
    public Geolocation(Context context, GeolocationCallback geolocationCallBack){
        this.context = context;
        this.geolocationCallBack = geolocationCallBack;

        /* Запуск определения местоположения. */
        startTrackingLocation();
    }

    /** Коллбэк, вызываемый при определении местоположения. */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            /* Если активити, на которой работало определение местоположения,
             * уничтожается, то остановить определение местоположения. */
            if (((Activity)context).isDestroyed())
                stopTrackingLocation();

            for (Location location : locationResult.getLocations()) {
                /* Вызываем коллбек активности, передавая в него данные о местоположении. */
                geolocationCallBack.onGeolocationResult(
                        location.getLatitude(),
                        location.getLongitude());
            }
        }
    };

    /** Метод получения местоположения. */
    public void startTrackingLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        /* Если нет разрешения на определение местоположения, то запросить его. */
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        /* Иначе, отслеживать местоположение пользователя. */
        else {
            /* Объект с настройками геолокации. */
            LocationRequest locationRequest = new LocationRequest();

            /* Интервал между запросами. */
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            /* Отслеживать местоположение пользователя. */
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
        }
    }

    /** Метод остановки получения метоположения. */
    private void stopTrackingLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
