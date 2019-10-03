package com.twoengers.mear.google_maps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.twoengers.mear.config.GameConfig;
import com.twoengers.mear.geodesy.Geodesy;
import com.twoengers.mear.places.ManagerPlaces;
import com.twoengers.mear.places.Place;
import com.twoengers.mear.player.Player;

import java.util.ArrayList;

/** Класс менеджера для управления маркерами на карте. */
public class PlaceMarkersManager implements GoogleMap.OnMarkerClickListener {
    /** Контекст, в котором создан объект. */
    private Context context;
    /** GoogleMap карта. */
    private GoogleMap googleMap;
    /** Коллбэк для вызова методов при взаимодействии с маркерами. */
    private MapEventCallback mapEventCallback;
    /** Список с объектами маркеров мест на карте. */
    private ArrayList<PlaceMarker> PlaceMarkers = new ArrayList<>();
    /** Широта центра области показа маркеров. */
    private double latitudeCenter = 0;
    /** Долгота центра области показа маркеров. */
    private double longitudeCenter = 0;

    PlaceMarkersManager(Context context, GoogleMap googleMap, MapEventCallback mapEventCallback){
        this.context = context;
        this.googleMap = googleMap;
        this.mapEventCallback = mapEventCallback;

        /* Устанавливаем слушатель нажатий на маркеры. */
        googleMap.setOnMarkerClickListener(this);
    }

    /**
     * Метод обновления области показа маркеров.
     * Входные парамеры:
     *      (double) latitudePlayer - текущая широта игрока;
     *      (double) longitudePlayer - текущая долгота игрока;
     *      (boolean) necessarily - обязательное обновление (при загрузке данных).
     */
    void updatePlaceMarkers(double latitudePlayer, double longitudePlayer){
        /* Если игрок отощел от текущего центра области показа маркеров
        * или обновление обязательно (necessarily) после загрузки новых мест. */
        if (Geodesy.distance(latitudePlayer, longitudePlayer, latitudeCenter, longitudeCenter) >
                GameConfig.distanceLastShowMarkers) {

            /* Обновить центр показа маркера на координаты игрока. */
            latitudeCenter = latitudePlayer;
            longitudeCenter = longitudePlayer;

            /* Удалить все текущие маркеры. */
            hidePlaceMarkers();
            /* Отобразить маркеры возле игрока. */
            showPlaceMarkers();
        }
    }

    /** Метод отображения маркеров возле игрока. */
    private void showPlaceMarkers(){
        /* Цикл по всем загруженным местам. */
        for (Place place : ManagerPlaces.getPlaces()){
            /* Если место находится в радиусе отображения игрока. */
            if (Geodesy.distance(place.getLatitude(), place.getLongitude(),
                    latitudeCenter, longitudeCenter) < GameConfig.distanceOutside)
                /* Добавить новый маркер. */
                PlaceMarkers.add(new PlaceMarker(context, googleMap, place));
        }
    }

    /** Метод удаления всех маркеров с карты. */
    private void hidePlaceMarkers(){
        /* Цикл по всем маркерам в списке. */
        for (PlaceMarker placeMarker : PlaceMarkers)
            /* Удаляем маркер с карты. */
            placeMarker.removeMarkerFromMap();

        /* Очищаем список маркеров. */
        PlaceMarkers.clear();
    }

    /** Метод, вызываемый при нажатии на маркер. */
    @Override
    public boolean onMarkerClick(Marker marker) {
        /* Получение ID маркера. */
        String markerID = marker.getTitle();
        /* Получение объекта игрового места по ID. */
        Place place = ManagerPlaces.getPlaceByID(markerID);

        /* Проверка игрового места:
        * Если оно взломано и время деактивации вышло - восстановить точку. */
        assert place != null;
        ManagerPlaces.checkPlace(place);

        /* Если объект находится в пределах окружности действия игрока. */
        if (Geodesy.distance(Player.getLatitude(), Player.getLongitude(),
                place.getLatitude(), place.getLongitude()) <= GameConfig.distanceInteraction){

            /* Если точка неактивна. */
            if (place.isHacked())
                /* Вызываем коллбек при нажатии на неактивную точку. */
                mapEventCallback.onClickDeactivationObject(place);

            else
                /* Вызываем коллбек при нажатии на активную точку. */
                mapEventCallback.onClickActivationObject(place);

        }

        /* Если объект находится за пределами окружности действия игрока. */
        else

            /* Вызываем коллбек при нажатии на удалённую точку. */
            mapEventCallback.onClickRemoteObject();

        return true;
    }
}
