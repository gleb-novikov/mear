package com.twoengers.mear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoengers.mear.geolocation.Geolocation;
import com.twoengers.mear.geolocation.GeolocationCallback;
import com.twoengers.mear.google_maps.MapManager;
import com.twoengers.mear.overpass_api.DownloadCallback;
import com.twoengers.mear.overpass_api.DownloadingPlaces;
import com.twoengers.mear.places.ManagerPlaces;
import com.twoengers.mear.places.Place;
import com.twoengers.mear.player.Player;

import es.dmoral.toasty.Toasty;

public class MapActivity extends AppCompatActivity
        implements GeolocationCallback, DownloadCallback {



    /** Объект класса MapManager для работы с картой. */
    private MapManager mapManager;

    /** Объект класса Geolocation для получения местоположения пользователя. */
    private Geolocation geolocation;

    /** Объект класса DownloadingPlaces для загрузки гео-данных. */
    private DownloadingPlaces downloadingPlaces;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        /* Загрузка данных игрока из локального хранилища. */
        Player.loadFromStorage(this);

        /* Инициализация менеджера карты. */
        mapManager = new MapManager(this, R.id.map);

        /* Обновление конекста у менеджера игровых мест. */
        ManagerPlaces.loadFromStorage(this);

        /* Определение местоположения. */
        geolocation = new Geolocation(this, this);

        /* Инициализация объекта для загрузки гео-данных. */
        downloadingPlaces = new DownloadingPlaces(this, this);

        /* Обновляем местоположение пользователя на карте. */
        mapManager.updatePlayerLocation(Player.getLatitude(), Player.getLongitude());
    }



    @Override
    protected void onResume() {
        super.onResume();
        /* Инициализация активности. */
        initializationActivity();
    }



    /** Метод, инициализирующий все необходимые компоненты активити. */
    private void initializationActivity(){
        /* Обновление контекста у игрока. */
        Player.setContext(this);

        /* Обновление конекста у менеджера игровых мест. */
        ManagerPlaces.setContext(this);
    }



    /** Метод отлеживания нажатия на кнопки интерфейса при обычной игре.. */
    private View.OnClickListener onClickUIButtons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            /* Запускаем анимацию. */
//            v.startAnimation(AnimationUtils.loadAnimation(MapActivity.this,
//                    R.anim.click_button));
//
//            /* Объект класса Intent - для перехода между активностями. */
//            Intent intent = null;
//
//            switch (v.getId()){
//                case R.id.buttonProfile:
//                    intent = new Intent(MapActivity.this, ProfileViewActivity.class);
//                    break;
//                case R.id.buttonMessenger:
//                    intent = new Intent(MapActivity.this, ContactActivity.class);
//                    break;
//                case R.id.buttonStore:
//                    intent = new Intent(MapActivity.this, StoreActivity.class);
//                    break;
//                case R.id.buttonCamera:
//                    if (hackedRegionsManager.isOutsideHackedRegion(Player.getLatitude(),
//                            Player.getLongitude())) {
//                        intent = new Intent(MapActivity.this,
//                                ARDetectionActivity.class);
//                    } else {
//                        mapManager.showHackedRegions(hackedRegionsManager.getHackedRegions());
//
//                        /* Текст сообщения о том, что необходимо подождать. */
//                        String message = getResources().getString(R.string.toasty_hack);
//                        /* Цвет тоасти. */
//                        int tintColor = getResources().getColor(R.color.toasty);
//                        /* Цвет текста. */
//                        int textColor = getResources().getColor(R.color.textLight);
//
//                        /* Вывод тоасти с сообщением. */
//                        Toasty.custom(MapActivity.this, message,
//                                getResources().getDrawable(R.drawable.toasty_icon_time),
//                                tintColor, textColor, Toasty.LENGTH_SHORT, true, true).show();
//                    }
//                    break;
//            }
//
//            /* Запускаем активность. */
//            if (intent != null) {
//                startActivity(intent);
//            }
//
//            if (v.getId() == R.id.buttonCamera){
//                /* Устанавливаем анимацию для перехода между активностями. */
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//            }
//            else {
//                /* Устанавливаем анимацию для перехода между активностями. */
//                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
//            }
        }
    };



    /** Метод, вызываемый при получении результата из активности с мини-игрой, при обучении. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        /* Если игра выйграна. */
//        if (data.getBooleanExtra("isWinGame", false))
//            new LearningSystem(MapActivity.this, LearningSystem.PHASE_TREE);
//            /* Если игра проиграна. */
//        else {
//            Toasty.custom(MapActivity.this, getString(R.string.learning_lose_game),
//                    getResources().getDrawable(R.drawable.toasty_hack),
//                    getResources().getColor(R.color.toasty),
//                    getResources().getColor(R.color.textLight),
//                    Toasty.LENGTH_SHORT, true, true).show();
//        }
    }



    /** Метод, вызываемый при получении данных о местоположении игрока. */
    @Override
    public void onGeolocationResult(double latitude, double longitude) {
        /* Обновляем данные о местоположении игрока. */
        Player.setLatitude(latitude);
        Player.setLongitude(longitude);

        /* Обновляем местоположение пользователя на карте. */
        mapManager.updatePlayerLocation(latitude, longitude);

        /* Проверяем необходимость подгрузки гео-данных. */
        downloadingPlaces.checkNeedDownload(latitude, longitude);
    }



    /** Метод, вызываемый при ответе пользователя на запрос разрешения. */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /* Разрешение на определение местоположения. */
        if (requestCode == geolocation.REQUEST_LOCATION_PERMISSION){
            /* Если разрешение получено, запросить местоположение. */
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geolocation.startTrackingLocation();
            }
        }
    }



    /** Метод, вызываемый при завершении загрузки гео-данных. */
    @Override
    public void onDownloadComplete(double latitude, double longitude) {
        /* Обновляем маркеры на карте. */
        mapManager.updatePlaceMarkers(latitude, longitude);
    }
}
