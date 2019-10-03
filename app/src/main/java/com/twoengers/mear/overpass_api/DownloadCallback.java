package com.twoengers.mear.overpass_api;

/** Интерфейс для реализации коллбэка при загрузке данных OpenStreetMap. */
public interface DownloadCallback {
    /** Метод, вызываемый при завершении загрузки. */
    void onDownloadComplete(double latitude, double longitude);
}
