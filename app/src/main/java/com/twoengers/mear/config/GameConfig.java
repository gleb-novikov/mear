package com.twoengers.mear.config;

/** Класс, содержащий игровые параметры. */
public class GameConfig {
    /** Дистанция, на которой игрок может взаимодействовать с объектами на карте (в метрах). */
    public static int distanceInteraction = 150;
    /** Радиус загрузки картографических OpenStreetMap данных возле игрока (в метрах). */
    public static int distanceDownloading = 1500;
    /** Дистанция, при выходе за которую начинается подгрузка объектов карты (в метрах). */
    public static int distanceOutside = 500;
    /** Дистанция, при выходе за которую обновляется показ маркеров на карте. */
    public static int distanceLastShowMarkers = 200;
    /** Минимальная дистанция между объектами на карте (в метрах). */
    public static int distanceOverlay = 50;
    /** Время деактивации точки после взлома (в секундах). */
    public static int timeDeactivation = 3 * 60 * 60;
    /** Название ключа локального хранилища с данными игрока. */
    public static String keyStoragePlayer = "hacked_player";
    /** Название ключа локального хранилища с данными о местах. */
    public static String keyStoragePlaces = "hacked_places";
}
