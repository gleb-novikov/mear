package com.twoengers.mear.geolocation;

/** Интерфейс для реализации коллбэка, при определении местоположения. */
public interface GeolocationCallback {
    /**
     * Метод, вызываемый при определении местоположения.
     * Входные параметры:
     *     (double) latitude - широта игрока;
     *     (double) longitude - долгота игрока.
     */
    void onGeolocationResult(double latitude, double longitude);
}
