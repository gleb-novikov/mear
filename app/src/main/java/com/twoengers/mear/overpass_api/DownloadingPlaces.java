package com.twoengers.mear.overpass_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.twoengers.mear.config.GameConfig;
import com.twoengers.mear.geodesy.Geodesy;
import com.twoengers.mear.places.ManagerPlaces;
import com.twoengers.mear.places.Place;

import java.util.ArrayList;

import static com.twoengers.mear.overpass_api.GetOverpassService.GET_OVERPASS_DATA;

/** Класс для загрузки данных об объектах карты с OpenStreetMap. */
public class DownloadingPlaces {
    /** Контекст, в котором создан объект класса. */
    private Context context;
    /** Коллбэк, который будет вызываться при завершении загрузки. */
    private DownloadCallback downloadCallback;
    /** Объект для формирования строки запроса к серверу и обработки результата. */
    private OverpassHelper overpassHelper;
    /** Широта последней загрузки данных. */
    private double latitudeCenter;
    /** Долгота последней загрузки данных. */
    private double longitudeCenter;
    /** Текущий этап загрузки (т.к. загрузки идёт последовательно). */
    private byte stepsDownloading = 0;
    /** Текущее состояние (происходит ли сейчас загрузка или нет). */
    private boolean isDownload;

    public DownloadingPlaces(Context context, DownloadCallback downloadCallback){
        this.context = context;
        this.downloadCallback = downloadCallback;

        /* Инициализируем объект OverpassHelper-а. */
        overpassHelper = new OverpassHelper();
    }

    /**
     * Метод проверки необходимости загрузки картографических данных.
     * Если необходимо, то начинает загружать.
     * Входные параметры:
     *      (double) latitudePlayer - текущая широта игрока;
     *      (double) longitudePlayer - текущая долгота игрока.
     */
    public void checkNeedDownload(double latitudePlayer, double longitudePlayer){
        /* Если сейчас уже не происходит загрузка, и игрок отошел от прошлого места загрузки. */
        if (!isDownload && (ManagerPlaces.getPositionDownload().size() == 0 ||
                ManagerPlaces.isOutsidePositionDownload(latitudePlayer, longitudePlayer) ||
                Geodesy.distance(latitudeCenter, longitudeCenter, latitudePlayer, longitudePlayer) >
                GameConfig.distanceOutside)){
            /* Загрузка началась. */
            isDownload = true;
            /* Обновляем координаты загрузки. */
            latitudeCenter = latitudePlayer;
            longitudeCenter = longitudePlayer;
            /* Начинаем загружать картографические данные. */
            startDownload();
            Log.d("DOWNLOAD_TEST", "START");
        }
    }

    /** Метод начала загрузки OpenStreetMap данных объектов. */
    private void startDownload(){

        /* Добавляем новую скаченную область. */
        ManagerPlaces.addPositionDownload(new double[]{latitudeCenter, longitudeCenter});

        /* Регистрируем ресивер на прослушивание определенного Intent. */
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(GET_OVERPASS_DATA));

        /* Цикл для загрузки всех типов объектов. */
        downloadTypeOfPlace((byte) 0);
    }

    public void stopDownload(){
        /* Снимаем метку загрузки. */
        isDownload = false;

        /* Отвязываем Receiver. */
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Метод последовательной загрузки разных типов объектов.
     * Входные параметры:
     *      (byte) type - текущий тип объектов для загрузки.
     */
    private void downloadTypeOfPlace(byte type){
        /* Получение строки запроса к серверу, при помощи OverpassHelper. */
        String urlOSM = overpassHelper.getUrlOSM(latitudeCenter, longitudeCenter, type);

        /* Объявляем новый интент для загрузки данных через сервис. */
        Intent intent = new Intent();

        /* Передаём тип загружаемых данных. */
        intent.putExtra("dataType", type);
        /* Передаём список загружаемых данных. */
        intent.putExtra("data", urlOSM);

        intent.setClass(context, GetOverpassService.class);

        /* Запускаем сервис. */
        context.startService(intent);
    }

    /** BroadcastReceiver для прослушивания входящих сообщений с загруженными данными. */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* Получаем тип объектов. */
            byte dataType = intent.getByteExtra("dataType", (byte) 0);

            /* Получаем строку с объектами. */
            String overpassResult = intent.getStringExtra("overpassResult");

            /* Парсер для преобразования строки в список с объектами. */
            ArrayList<Place> placesDownloaded = overpassHelper.parseXML(overpassResult, dataType);

            /* Добавление скаченных объектов в список. */
            for (Place place: placesDownloaded){
                /* Если такого объекта ещё не было. */
                if (ManagerPlaces.getPlaceByID(place.getId()) == null)
                    ManagerPlaces.addPlace(place);
            }

            /* Увеличение прогрусса загрузки. */
            stepsDownloading++;

            /* Если данные о всех типах объектов загружены. */
            if (stepsDownloading == 7){
                /* Очистка близкорасположенных объектов. */
                ManagerPlaces.removePointsOverlay();

                /* Сбрасываем прогресс загрузки. */
                stepsDownloading = 0;

                /* Отправляем информацию в активность об окончании загрузки. */
                downloadCallback.onDownloadComplete(latitudeCenter, longitudeCenter);

                /* Снимаем метку загрузки. */
                isDownload = false;

                /* Отвязываем Receiver. */
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);

                Log.d("DOWNLOAD_TEST", "STOP");
            }
            else {
                /* Продолжить загрузку данных для следущего типа объектов. */
                downloadTypeOfPlace((byte) (dataType + 1));
                Log.d("DOWNLOAD_TEST", String.valueOf(dataType));
            }
        }
    };
}
