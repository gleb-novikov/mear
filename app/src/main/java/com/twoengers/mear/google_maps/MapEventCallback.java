package com.twoengers.mear.google_maps;

import com.google.android.gms.maps.GoogleMap;
import com.twoengers.mear.places.Place;

/**
 * Интерфейс для реализации коллбэка, при различных действиях с картой.
 * @author Глеб Новиков
 * @version 1.0
 */
public interface MapEventCallback {
    /**
     * Метод, вызываемый при загрузке Google Карты.
     * @param googleMap инициализированная Google Карта
     */
    void onMapViewReady(GoogleMap googleMap);

    /**
     * Метод, вызываемый при нажатии на далёкий объект.
     */
    void onClickRemoteObject();

    /**
     * Метод, вызываемый при нажатии на взломанный объект.
     * @param place игровое место
     */
    void onClickDeactivationObject(Place place);

    /**
     * Метод, вызываемый при нажатии на доступный объект.
     * @param place игровое место
     */
    void onClickActivationObject(Place place);
}
