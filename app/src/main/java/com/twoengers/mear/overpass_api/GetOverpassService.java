package com.twoengers.mear.overpass_api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Класс сервиса для ассинхронной загрузки OpenStreetMap данных. */
public class GetOverpassService extends IntentService {
    /** Тег для ведения логов. */
    private static final String TAG = "download_overpass_data";
    /** Индификатор для отлова звгруженных данных. */
    public static final String GET_OVERPASS_DATA = "com.twoengers.mear.GET_OVERPASS_DATA";
    /** Ссылка на сервер Overpass. */
    private static final String BASE_URL = "https://overpass.kumi.systems/";
    /** Тип загружаемых объектов. */
    private byte dataType;

    public GetOverpassService() {
        super("GetOverpassService");
    }

    /** Метод, вызываемый при запросе загрузки. */
    @Override
    protected void onHandleIntent(Intent intent) {
        /* Определение типа загружаемых объектов. */
        dataType = intent.getByteExtra("dataType", (byte) 0);
        /* Строка с загруженными данными. */
        String overpassResult;
        Log.d(TAG, "START");

        /* Загрузка данных. */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OverpassService service = retrofit.create(OverpassService.class);
        /* Добавление в запрос информации о нужных объектах. */
        Call<ResponseBody> call = service.getData(intent.getStringExtra("data"));
        try {
            Response<ResponseBody> userResponse = call.execute();
            assert userResponse.body() != null;
            /* Получение строки с загруженными данными. */
            overpassResult = userResponse.body().string();
            /* Вызов метода отправки данных в активность. */
            sendOverpassDataInActivity(overpassResult);
            stopSelf();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            stopSelf();
        }
    }

    /** Метод отправки загруженных данных в активность. */
    private void sendOverpassDataInActivity(String overpassResult) {
        Intent intent = new Intent(GET_OVERPASS_DATA);
        /* Добавляем тип данных. */
        intent.putExtra("dataType", dataType);
        /* Добавляем данные. */
        intent.putExtra("overpassResult", overpassResult);
        /* Отправляем данные. */
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        /* Останавливаем сервис. */
        stopSelf();
    }
}
