package com.twoengers.mear.google_maps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.twoengers.mear.config.GameConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс маркера игрока на GoogleMap.
 * @author Глеб Новиков
 * @version 1.0
 */
class PlayerMarker {
    /** Контекст, в котором создан объект маркера игрока. */
    private Context context;
    /** GoogleMap на которой отображается маркер игрока. */
    private GoogleMap googleMap;
    /** Широта маркера игрока. */
    private double latitude;
    /** Долгота маркера игрока. */
    private double longitude;
    /** Маркер игрока (окружность!). */
    private Circle marker;
    /** Окружность, показывающая радиус действия игрока на карте. */
    private Circle circleAction;
    /** Изменяющееся значение радиуса окружности действия игрока. */
    private int circleActionRadius;
    /** Изменяющееся значение прозрачности окружности действия игрока. */
    private int circleActionAlpha;
    /** Таймер, для активации анимации окружности действия игрока. */
    private Timer timerCircleAction;
    /** Таймер, выполняющий плавное увеличение окружности действия игрока. */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /* Если радиус действия < радиуса действия из конфигурационного файла,
                     * т.е. окружность ещё растёт. */
                    if (circleActionRadius < (GameConfig.distanceInteraction)){
                        /* Увеличиваем радиус. */
                        circleActionRadius += 5;
                        /* Уменьшаем прозрачность. */
                        circleActionAlpha -= 5;

                        /* Обновляем положение окружности на карте. */
                        circleAction.setCenter(new LatLng(latitude, longitude));

                        /* Устанавливаем радиус. */
                        circleAction.setRadius(circleActionRadius);
                        /* Устанавливаем заливку и обводку, исходя из значения прозрачности. */
                        circleAction.setFillColor(Color.argb(circleActionAlpha,
                                192,95,194));
                        circleAction.setStrokeColor(Color.argb(2 * circleActionAlpha,
                                192,95,194));
                    }
                    else {
                        /* Удаляем окружность игрока. */
                        circleAction.remove();
                        circleAction = null;
                        /* Останавливаем таймер. */
                        timerCircleAction.cancel();
                    }
                }
            });
        }
    };

    /**
     * Конструктор класса.
     * @param context контекст, в котором создаётся маркер игрока
     * @param googleMap Google Карта, на которой размещается маркер игрока
     */
    PlayerMarker(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
    }

    /**
     * Метод обновления координат маркета игрока.
     * @param latitude координата широты игрока
     * @param longitude координата долготы игрока
     */
    void updateMarkerPosition(double latitude, double longitude){
        /* Получение широты и долготы игрока. */
        this.latitude = latitude;
        this.longitude = longitude;
        /* Отрисовка маркера игрока. */
        if (marker == null) {
            /* Если это первая отрисовка, создавать новый маркер. */
            marker = googleMap.addCircle(new CircleOptions()
                    .radius(2.5)
                    .fillColor(Color.rgb(255, 255, 255))
                    .strokeColor(Color.rgb(192, 95, 194))
                    .strokeWidth(6)
                    .center(new LatLng(this.latitude, this.longitude)));
        } else {
            /* Иначе, перемещать маркер игрока. */
            marker.setCenter(new LatLng(latitude, longitude));
        }
    }

    /**
     * Метод отрисовки окружности действия игрока.
     */
    void drawPlayerRadius(){
        /* Если на данный момент окружность действия отсутвует на карте. */
        if (circleAction == null){
            /* Устанавливаем минимальный радиус окружности действия. */
            circleActionRadius = 0;
            /* Устанавливаем максимальную прозрачность окружности действия. */
            circleActionAlpha = 100;

            /* Рисуем окружность на карте. */
            circleAction = googleMap.addCircle(new CircleOptions()
                    .radius(circleActionRadius)
                    .fillColor(Color.argb(circleActionAlpha, 192,95,194))
                    .strokeColor(Color.argb(2 * circleActionAlpha, 192,95,194))
                    .strokeWidth(3)
                    .center(new LatLng(latitude, longitude)));

            /* Таймер для плавного увеличения и растворения окружности действия игрока. */
            timerCircleAction = new Timer();

            /* Запускаем таймер сейчас (0) и после этого каждые 50 милисекунд. */
            timerCircleAction.scheduleAtFixedRate (timerTask, 0, 50);
        }
    }
}
