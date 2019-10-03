package com.twoengers.mear.places;

import android.content.Context;
import android.content.SharedPreferences;

import com.twoengers.mear.config.GameConfig;

/** Класс для сохранения данных об объектах карты, при помощи SharedPreferences. */
class StoragePlaces {
    /** Объект класса SharedPreferences для сохранения данных. */
    private SharedPreferences preferences;

    StoragePlaces(Context context){
        /* Загрузка сохранения с ключем hacked_places. */
        preferences = context.getSharedPreferences(GameConfig.keyStoragePlaces, Context.MODE_PRIVATE);
    }

    /** Метод сохрания списка взломанных точек. */
    void savePlaces(String places){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("places", places);
        editor.apply();
    }

    /** Метод загрузки списка взломанных точек. */
    String loadPlaces(){
        return preferences.getString("places", "");
    }
}
