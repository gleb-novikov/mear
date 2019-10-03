package com.twoengers.mear.places;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twoengers.mear.R;
import com.twoengers.mear.config.GameConfig;
import com.twoengers.mear.geodesy.Geodesy;

import java.util.ArrayList;

/** Класс менеджера для управления игровыми местами карты. */
public class ManagerPlaces {
    /** Список со всеми игровыми местами карты. */
    private static ArrayList<Place> places;
    /** Список координат загруженных областей. */
    private static ArrayList<double[]> positionDownload;
    /** Объект для сохранения и загрузки списка игровых мест. */
    private static StoragePlaces storagePlaces;

    /** Метод загрузки списка игровых мест из локального хранилища. */
    public static void loadFromStorage(Context context){
        /* Инициализация списка с координатами загруженных областей. */
        positionDownload = new ArrayList<>();

        /* Загрузка сохранённого списка объектов. */
        storagePlaces = new StoragePlaces(context);
        addPlacesFromJSON(storagePlaces.loadPlaces());

        /* Удаление взломанных точек, у которых вышло время деактивации. */
        checkPlaces();
    }

    /** Метод, устанавливающий текущий контекст. */
    public static void setContext(Context context){
        /* Обновление объекта для синхронизации списка с хранилищем с новым контекстом. */
        storagePlaces = new StoragePlaces(context);
    }

    /** Метод, возвращающий список координат загруженных областей. */
    public static ArrayList<double[]> getPositionDownload(){
        return positionDownload;
    }

    /** Метод, добовляющий новые координаты в список загруженных областей. */
    public static void addPositionDownload(double[] position) {
        positionDownload.add(position);
    }

    /**
     * Метод, проверяющий не вышел ли игрок за области с загруженными гео-данными.
     * Входные параметры:
     *      (double) latitude - широта игрока;
     *      (double) longitude - долгота игрока.
     * Возвращаемое значение:
     *      (boolean) isOutside - true если игрок находится за пределами загруженных областей.
     */
    public static boolean isOutsidePositionDownload(double lat, double lon){
        /* Последовательно проверяем каждую область. */
        for (double[] position: positionDownload){
            /* Если игрок находится в области, возвращаем false. */
            if (Geodesy.distance(lat, lon, position[0], position[1]) < GameConfig.distanceOutside)
                return false;
        }
        /* Если игрок выщел за область или областей нет возвращаем true. */
        return true;
    }

    /** Метод получения списка всех игровых мест. */
    public static ArrayList<Place> getPlaces() {
        return places;
    }

    /**
     * Метод получения конкретного места по ID.
     * Входные параметры:
     *      (String) id - ID игрового места.
     * Возвращаемое значение:
     *      (Place) place - игровое место (null если не найдено).
     */
    public static Place getPlaceByID(String id){
        for (Place place: places){
            if (place.getId().equals(id))
                return place;
        }
        return null;
    }

    /** Метод добавления нового игрового места в список. */
    public static void addPlace(Place place){
        places.add(place);
    }

    /** Метод проверки состояния игрового места. */
    public static void checkPlace(Place place){
        /* Если место взломано. */
        if (place.isHacked()){
            /* Текущее время в секундах. */
            long timestamp = System.currentTimeMillis() / 1000;

            /* Если время деактивации вышло. */
            if (timestamp - place.getTimestamp() >= GameConfig.timeDeactivation){
                /* Делаем место опять активным. */
                place.setHacked(false);
            }
        }
    }

    /** Метод проверки состояния всех игровых мест. */
    private static void checkPlaces(){
        /* Текущее время в секундах. */
        long timestamp = System.currentTimeMillis() / 1000;

        /* Цикл для прохождения по всем игровым местам. */
        for (Place place : new ArrayList<>(places)) {
            /* Если точка взломана и время деактивации вышло, то активировать её. */
            if ((place.isHacked()) && (timestamp - place.getTimestamp() >= GameConfig.timeDeactivation)){
                place.setHacked(false);
            }
        }

        /* Сохранение взломанных точек. */
        storagePlaces.savePlaces(getJSONHackedPlaces());
    }

    /** Метод взлома игрового места. */
    public static void hackPlace(Place place, Long timestamp) {
        /* Точка взломана. */
        place.setHacked(true);
        /* Время деактивации. */
        place.setTimestamp(timestamp);

        /* Сохранение взломанных точек. */
        storagePlaces.savePlaces(getJSONHackedPlaces());
    }

    /** Метод получения списка взломанных точек из JSON строки. */
    private static void addPlacesFromJSON(String json) {
        if (json.equals(""))
            places = new ArrayList<>();
        else
            places = new Gson().fromJson(json,
                    new TypeToken<ArrayList<Place>>() {}.getType());
    }

    /** Метод получения JSON строки из списка взломанных точек. */
    private static String getJSONHackedPlaces(){
        /* Локальный список взломанных точек. */
        ArrayList<Place> hackedPlaces = new ArrayList<>();

        /* Перебор всех игровых мест. */
        for (Place place: places){
            /* Если место взломано, то добавляем его в список. */
            if (place.isHacked())
                hackedPlaces.add(place);
        }

        /* Получаем JSON строку из списка. */
        return new Gson().toJson(hackedPlaces);
    }

    /**
     * Метод, возвращающий оставшееся время деактивации точки.
     * Входные параметры:
     *      (Place) place - объект места.
     * Возвращает:
     *      (String) time - строку со временем (дни, часы, минуты, секунды).
     */
    public static String getRemainingTime(Context context, Place place){
        /* Текущее время в секундах. */
        long timestamp = System.currentTimeMillis() / 1000;

        /* Оставшееся время в секундах. */
        long remainingTime = GameConfig.timeDeactivation - (timestamp - place.getTimestamp());

        /* Время в текстовом представлении:
         * _с | _мин _с | _мин | _ч _мин | _ч | _дн _ч | _дн. */
        String time;

        /* Если время меньше минуты (60 секунд). */
        if (remainingTime < 60)
            /* Вид строки: _с. */
            time = remainingTime + context.getResources().getString(R.string.time_second);

        /* Если время меньше 6 минут (6 * 60 секунд). */
        else if (remainingTime < 6 * 60){
            /* Вычисляем минуты. */
            long minutes = remainingTime / 60;
            /* Вычисляем секунды. */
            long seconds = remainingTime - 60 * minutes;
            /* Вид строки: _мин _с. */
            time = minutes + context.getResources().getString(R.string.time_minute) + " " +
                    seconds + context.getResources().getString(R.string.time_second);
        }

        /* Если время меньше часа (60 * 60 секунд). */
        else if (remainingTime < 60 * 60)
            /* Вид строки: _мин. */
            time = remainingTime / 60 + context.getResources().getString(R.string.time_minute);

        /* Если время меньше 6 часов (6 * 60 * 60 секунд). */
        else if (remainingTime < 6 * 60 * 60){
            /* Вычисляем часы. */
            long hours = remainingTime / (60 * 60);
            /* Вычисляем минуты. */
            long minutes = remainingTime / 60 - 60 * hours;
            /* Вид строки: _ч _мин. */
            time = hours + context.getResources().getString(R.string.time_hour) + " " +
                    minutes + context.getResources().getString(R.string.time_minute);
        }

        /* Если время меньше 24 часов (24 * 60 * 60 секунд). */
        else if (remainingTime < 24 * 60 * 60)
            /* Вид строки: _ч. */
            time = remainingTime / (60 * 60) + context.getResources().getString(R.string.time_hour);

        /* Если время меньше 3 дней (3 * 24 * 60 * 60 часов). */
        else if (remainingTime < 3 * 24 * 60 * 60){
            /* Вычисляем дни. */
            long days = remainingTime / (24 * 60 * 60);
            /* Вычисляем часы. */
            long hours = remainingTime / (60 * 60) - 24 * days;
            /* Вид строки: _дн _ч. */
            time = days + context.getResources().getString(R.string.time_day) + " " +
                    hours + context.getResources().getString(R.string.time_hour);
        }

        /* Если время больше 3 дней. */
        else
            /* Вид строки: _дн. */
            time = remainingTime / (24 * 60 * 60) + context.getResources().getString(R.string.time_day);

        return time;
    }

    /** Метод, удаляющий близкорасположенные игровые места. */
    public static void removePointsOverlay(){
        /* Цикл прохождения по всем игровым местам. */
        for (int i = 0; i < places.size(); i++){

            /* Получение широты и долготы первого игрового места. */
            float lat1 = places.get(i).getLatitude();
            float lon1 = places.get(i).getLongitude();

            /* Цикл прохождения по всем последующим местам. */
            for (int j = i + 1; j < places.size(); j++){

                /* Получение широты и долготы второго игрового места. */
                float lat2 = places.get(j).getLatitude();
                float lon2 = places.get(j).getLongitude();

                /* Если первое и второе игровое место близко расположены, удаляем второе место. */
                if (Geodesy.distance(lat1, lon1, lat2, lon2) < GameConfig.distanceOverlay){
                    places.remove(j);
                    j--;
                }
            }
        }
    }
}
