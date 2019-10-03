package com.twoengers.mear.google_maps;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.twoengers.mear.R;

/**
 * Класс для отображения GoogleMap.
 * @author Глеб Новиков
 * @version 1.0
 */
public class MapView implements OnMapReadyCallback {
    /** Контекст, в котором создаётся GoogleMap. */
    private Context context;
    /** Google Карта. */
    private GoogleMap googleMap;
    /** Коллбэк, вызываемый при инициализации карты. */
    private MapEventCallback mapEventCallback;
    /** Ссылка на файл ресурсов с описанием стиля карты. */
    private int resourceMapStyle = R.raw.map_style;

    /**
     * Конструктор класса MapView.
     * @param context контекст, в котором создаётся карта
     * @param resourceID индентификатор MapFragment (например R.id.map)
     * @param mapEventCallback коллбэк для сообщения об инициализации карты
     */
    MapView(Context context, int resourceID, MapEventCallback mapEventCallback){
        this.context = context;
        this.mapEventCallback = mapEventCallback;
        /* Инициализация GoogleMap (после вызывается метод onMapReady). */
        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity)context)
                .getSupportFragmentManager().findFragmentById(resourceID);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Метод, вызываемый, когда карта будет инициализирована.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        /* Кастомизируем карту. */
        customize();
        /* Устанавливаем настройки карты. */
        setUiSettings();
        /* Сообщаем MapManager о том, что карта инициализирована. */
        mapEventCallback.onMapViewReady(googleMap);
    }

    /**
     * Метод кастомизации GoogleMap.
     */
    private void customize(){
        /* Установка кастомного стиля карты из ресурсов приложения. */
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, resourceMapStyle));
    }

    /**
     * Метод установки настроек GoogleMap.
     */
    private void setUiSettings(){
        /* Отключение панели инструментов карты. */
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        /* Отключение кнопки с определением местоположения. */
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        /* Отключение перемещения по карте. */
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        /* Отключение кнопки компаса. */
        googleMap.getUiSettings().setCompassEnabled(false);
    }
}
