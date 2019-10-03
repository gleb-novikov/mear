package com.twoengers.mear.google_maps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoengers.mear.R;
import com.twoengers.mear.places.Place;

/** Класс маркера места на карте. */
class PlaceMarker {
    /** Контекст, в котором создана GoogleMap. */
    private Context context;
    /** GoogleMap карта. */
    private GoogleMap googleMap;
    /** Объект места. */
    private Place place;
    /** Маркер места. */
    private Marker marker;

    /**
     * Конструктор класса PlaceMarker.
     * Входные параметры:
     *      (Context) context - контекст, в котором создана Google Карта;
     *      (GoogleMap) googleMap - Google Карта, на которой необходимо разместить маркер;
     *      (Place) place - объект игрового места.
     */
    PlaceMarker(Context context, GoogleMap googleMap, Place place){
        this.context = context;
        this.googleMap = googleMap;
        this.place = place;

        /* Добавляем маркер на карту. */
        addMarkerToMap();
    }

    /** Метод добавления маркера на карту. */
    private void addMarkerToMap(){
        /* Получение ширины и высоты экрана. */
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int weightDisplay = size.x;
        int heightDisplay = size.y;

        /* Коэфициент уменьшения маркера относительно экрана. */
        float coefficient = 0.03f;
        /* Сумма ширины и высоты экрана. */
        int sumSize = weightDisplay + heightDisplay;

        /* Установка размера иконки (ширина = высота). */
        int iconSize = (int) (coefficient * sumSize);

        /* Ресурс иконки маркера. */
        BitmapDrawable bitmapDrawable = null;

        /* Определение типа объекта, для подгрузки нужного изображения из ресурсов. */
        switch (place.getType()){
            case (0):
                /* Иконка объекта культуры. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_culture);
                break;
            case (1):
                /* Иконка гражданского объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_civil);
                break;
            case (2):
                /* Иконка медицинского объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_medicine);
                break;
            case (3):
                /* Иконка транспортного объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_transport);
                break;
            case (4):
                /* Иконка коммерческого объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_bank);
                break;
            case (5):
                /* Иконка образовательного объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_education);
                break;
            case (6):
                /* Иконка торгового объекта. */
                bitmapDrawable = (BitmapDrawable)context.getResources()
                        .getDrawable(R.drawable.marker_store);
                break;
        }

        /* Установка выбранного изображения. */
        Bitmap icon = null;
        if (bitmapDrawable != null)
            icon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconSize, iconSize,
                    false);

        /* Добавление маркера на карту. */
        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                .title(place.getId())
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
        );
    }

    /** Метод удаления маркера с карты. */
    void removeMarkerFromMap(){
        marker.remove();
    }
}
