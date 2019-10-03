package com.twoengers.mear.google_maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.twoengers.mear.ARActivity;
import com.twoengers.mear.R;
import com.twoengers.mear.overpass_api.DownloadingPlaces;
import com.twoengers.mear.places.ManagerPlaces;
import com.twoengers.mear.places.Place;
import com.twoengers.mear.player.Player;

import es.dmoral.toasty.Toasty;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/** Класс менеджера для управления всеми состовляющими карты. */
public class MapManager implements MapEventCallback {
    /** Контекст, в котором создан объект класса. */
    private Context context;
    /** Google Карта. */
    private GoogleMap googleMap;
    /** Маркер игрока на карте. */
    private PlayerMarker playerMarker;
    /** Менеджер для управления объектами на карте. */
    private PlaceMarkersManager placeMarkersManager;
    /** Переменная для проверки завершения анимации камеры на карте. */
    private boolean wasCameraAnimation;
    /** Переменная для проверки необходимости отображения фейкового маркера игрока. */
    private boolean isNeedShowPlayer;
    private boolean isNewGame;

    /**
     * Конструктор класса MapManager.
     * Входные параметры:
     *     (Context) context - контекст, в котором создаётся карта;
     *     (int) resourceID - индификатор MapFragment на активности (например R.id.map).
     */
    public MapManager(Context context, int resourceID){
        this.context = context;
        /* Инициализация карты. */
        new MapView(context, resourceID, this);
    }

    public void setNewGame(){
        isNewGame = true;
    }

    /** Метод, вызываемый при инициализации карты. */
    @Override
    public void onMapViewReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        /* Инициализируем маркер игрока. */
        playerMarker = new PlayerMarker(context, googleMap);
        /* Инициализируем менеджер объектов карты. */
        placeMarkersManager = new PlaceMarkersManager(context, googleMap, this);

        /* Если нам необходимо отобразить местоположение игрока, при обучении. */
        if (isNewGame){
            /* Отрисовка маркера игрока. */
            playerMarker.updateMarkerPosition(Player.getLatitude(), Player.getLongitude());

            /* Обновляем отображение маркеров на карте, с обязательным флагом.
             * Т.е. все маркеры обновятся, независимо от прошлого места обновления.*/
            placeMarkersManager.updatePlaceMarkers(Player.getLatitude(), Player.getLongitude());

            /* Объект камеры. */
            CameraPosition cameraPosition = new CameraPosition(new LatLng(Player.getLatitude(),
                    Player.getLongitude()), 17f, 35, 0);
            /* Перемещение камеры. */
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            /* Создаём ограничение для вращения камеры (чтобы маркер игрока всегда был в центре). */
            LatLngBounds playerBounds = new LatLngBounds(
                    new LatLng(Player.getLatitude(), Player.getLongitude()),
                    new LatLng(Player.getLatitude(), Player.getLongitude()));
            /* Применяем ограничение для карты. */
            googleMap.setLatLngBoundsForCameraTarget(playerBounds);

            /* Установка минимального и максимального зума. */
            googleMap.setMinZoomPreference(16);
            googleMap.setMaxZoomPreference(19);

            isNeedShowPlayer = false;
        }
        else
            updatePlayerLocation(Player.getLatitude(), Player.getLongitude());
    }

    /** Метод, вызываемый при нажатии на объект карты, который находиться далеко от игрока. */
    @Override
    public void onClickRemoteObject() {
        /* Вывод тоасти с сообщение о том, что объект находится слишком далеко. */
//        Toasty.custom(context, R.string.toasty_distance,
//                context.getResources().getDrawable(R.drawable.toasty_icon_distance),
//                R.color.toasty, R.color.textLight, Toasty.LENGTH_SHORT,
//                true, true).show();

        /* Рисование окружности действия игрока на карте. */
        playerMarker.drawPlayerRadius();
    }

    /** Метод, вызываемый при нажатии на неактивный объект карты. */
    @Override
    public void onClickDeactivationObject(Place place) {
//        /* Получение оставшегося времени деактивации точки. */
//        String remainingTime = ManagerPlaces.getRemainingTime(context, place);
//
//        /* Текст сообщения о том, что необходимо подождать. */
//        String message = context.getResources().getString(R.string.toasty_time) + ": " + remainingTime;
//        /* Цвет тоасти. */
//        int tintColor = context.getResources().getColor(R.color.toasty);
//        /* Цвет текста. */
//        int textColor = context.getResources().getColor(R.color.textLight);
//
//        /* Вывод тоасти с сообщением. */
//        Toasty.custom(context, message,
//                context.getResources().getDrawable(R.drawable.toasty_icon_time),
//                tintColor, textColor, Toasty.LENGTH_SHORT, true, true).show();
    }

    /** Метод, вызываемый при нажатии на активный объект карты. */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClickActivationObject(final Place place) {
        Intent intent = new Intent(context, ARActivity.class);
        context.startActivity(intent);
//        /* Окно с вариантами действия. */
//        LayoutInflater inflater = (LayoutInflater)
//                context.getSystemService(LAYOUT_INFLATER_SERVICE);
//        assert inflater != null;
//        @SuppressLint("InflateParams")
//        final View popupView = inflater.inflate(R.layout.window_hack, null);
//
//        /* Настройки окна. */
//        int width = LinearLayout.LayoutParams.MATCH_PARENT;
//        int height = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        /* Параметр focusable делает недоступными все элементы под окном. */
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
//
//        /* Отображаем окно. */
//        popupWindow.showAtLocation(
//                ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content),
//                Gravity.CENTER, 0, 0);
//
//        /* Текст заголовка окна. */
//        final TextView textView = popupView.findViewById(R.id.textLose);
//        /* Тип маркера. */
//        String markerTypeHeader =
//                place.getType() == 0 ? "Объект культуры" :
//                place.getType() == 1 ? "Гражданский объект" :
//                place.getType() == 2 ? "Медицинский центр" :
//                place.getType() == 3 ? "Транспортный объект" :
//                place.getType() == 4 ? "Коммерческий объект" :
//                place.getType() == 5 ? "Образовательный объект" :
//                "Торговый объект";
//
//        /* Устанавливаем текст на тип маркера. */
//        textView.setText(markerTypeHeader);
//
//        /* Получение размера экрана, для установки нужного размера шрифта текста. */
//        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int weightDisplay = size.x;
//
//        /* Устанавливаем размер шрифта относительно размера экрана. */
//        textView.setTextSize(weightDisplay / 40);
//
//        /* Круглый контейнер, в котором содержаться элементы интерфейса. */
//        final LinearLayout viewCircle = popupView.findViewById(R.id.viewCircle);
//
//        /* Кнопка взлома объекта. */
//        final Button buttonHack = popupView.findViewById(R.id.buttonHack);
//        /* Метод при нажатии на кнопку. */
//        buttonHack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewCircle.postOnAnimationDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        /* Переход на активность с мини-игрой для взлома объекта. */
//                        /* Объект класса Intent - для перехода между активностями. */
//                        Intent intent = new Intent(context, GameActivity.class);
//                        /* Отправляем текущий уровень игрока. */
//                        intent.putExtra("number", String.valueOf(Player.getLevel()));
//                        /* Отправляем ID маркера, чтобы в случае выйгрыша в мини-игре
//                         * добавить точку в список неактивных. */
//                        intent.putExtra("pointID", place.getId());
//
//                        /* Если эта обычная точка. */
//                        if (!place.getId().equals("0"))
//                            /* Запускаем новую активность с игрой. */
//                            context.startActivity(intent);
//                            /* Если это точка для обучения. */
//                        else
//                            /* Запускаем активность, и ждём от неё результата мини-игры. */
//                            ((Activity) context).startActivityForResult(intent, 1);
//
//                        /* Устанавливаем анимацию для перехода между активностями. */
//                        ((Activity)context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
//
//                        /* Закрытие окна. */
//                        popupWindow.dismiss();
//                    }
//                }, 300);
//
//
//                /* Запускаем анимацию. */
//                viewCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//                textView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//                buttonHack.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//            }
//        });
//
//        /* Запускаем анимацию. */
//        viewCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_in));
//        textView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_in));
//        buttonHack.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_in));
//
//        /* Метод вызываемый при нажатии на экран. */
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                /* Если нажатие за областью окна. */
//                if (!(event.getX() > viewCircle.getX() &&
//                        event.getX() < viewCircle.getX() + viewCircle.getWidth() &&
//                        event.getY() > viewCircle.getY() &&
//                        event.getY() < viewCircle.getY() + viewCircle.getHeight())) {
//
//                    viewCircle.postOnAnimationDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            /* Закрытие окна. */
//                            popupWindow.dismiss();
//                        }
//                    }, 300);
//
//                    /* Запускаем анимацию. */
//                    viewCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//                    textView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//                    buttonHack.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_out));
//                }
//
//                return true;
//            }
//        });
    }

    /** Метод обязательного обновления показа маркеров мест. */
    public void updatePlaceMarkers(double latitude, double longitude){
        /* Обновляем отображение маркеров на карте, с обязательным флагом.
        * Т.е. все маркеры обновятся, независимо от прошлого места обновления.*/
        placeMarkersManager.updatePlaceMarkers(latitude, longitude);
    }

    /** Метод, вызываемый при обновлении местоположения игрока. */
    public void updatePlayerLocation(double latitude, double longitude){
        /* Если карта ещё не загрузилась. */
        if (googleMap == null) {
            /* Отобразить маркер игрока при загрузке. */
            isNeedShowPlayer = true;
            return;
        }

        /* Отрисовка маркера игрока. */
        playerMarker.updateMarkerPosition(latitude, longitude);

        /* Минимальная координата для ограничения вращения при анимации камеры. */
        LatLng minLatLon = new LatLng(
                Math.min(latitude, googleMap.getCameraPosition().target.latitude),
                Math.min(longitude, googleMap.getCameraPosition().target.longitude));

        /* Максимальная координата для ограничения вращения при анимации камеры. */
        LatLng maxLatLon = new LatLng(
                Math.max(latitude, googleMap.getCameraPosition().target.latitude),
                Math.max(longitude, googleMap.getCameraPosition().target.longitude));

        /* Создаём ограничение для вращения камеры при её перемешении, от старой позиции к новой. */
        LatLngBounds playerBounds = new LatLngBounds(minLatLon, maxLatLon);
        /* Применяем ограничение для карты. */
        googleMap.setLatLngBoundsForCameraTarget(playerBounds);

        /* Объект камеры. */
        CameraPosition cameraPosition;

        if (!wasCameraAnimation){
            /* Установка начального положения камеры. */
            cameraPosition = new CameraPosition(new LatLng(latitude, longitude),
                    18f, 35, 0);
        }
        else {

            /* Установка текущих настроек камеры. */
            cameraPosition = new CameraPosition(new LatLng(latitude, longitude),
                    googleMap.getCameraPosition().zoom,
                    googleMap.getCameraPosition().tilt,
                    googleMap.getCameraPosition().bearing);
        }


        /* Плавное перемещение камеры (с вызовом callback, при завершении анимации). */
        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition), firstCameraAnimation);
    }

    /** Коллбек, вызываемый при завершении анимации камеры. */
    private GoogleMap.CancelableCallback firstCameraAnimation = new GoogleMap.CancelableCallback() {
        /** Метод, вызываемый при удачном завершении анимации камеры. */
        @Override
        public void onFinish() {
            /* Если это первая анимация. */
            if (!wasCameraAnimation) {
                /* Установка минимального и максимального зума. */
                googleMap.setMinZoomPreference(16);
                googleMap.setMaxZoomPreference(19);

                /* Анимация завершилась. */
                wasCameraAnimation = true;
            }

            /* Создаём ограничение для вращения камеры (чтобы маркер игрока всегда был в центре). */
            LatLngBounds playerBounds = new LatLngBounds(
                    new LatLng(Player.getLatitude(), Player.getLongitude()),
                    new LatLng(Player.getLatitude(), Player.getLongitude()));
            /* Применяем ограничение для карты. */
            googleMap.setLatLngBoundsForCameraTarget(playerBounds);

            /* Обновляем отображение маркеров на карте, с необязательным флагом.
             * Т.е. маркеры обновятся, только если игрок далеко ушел.*/
            if (DownloadingPlaces.firstLoad) {
                placeMarkersManager.updatePlaceMarkers(Player.getLatitude(), Player.getLongitude());
            }
        }

        /** Метод, вызываемый при неудачном завершении анимации камеры. */
        @Override
        public void onCancel() {}
    };
}
