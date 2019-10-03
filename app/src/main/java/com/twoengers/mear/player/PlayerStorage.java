package com.twoengers.mear.player;

import android.content.Context;
import android.content.SharedPreferences;

import com.twoengers.mear.config.GameConfig;

/** Класс для сохранения данных игрока, при помощи SharedPreferences. */
class PlayerStorage {
    /** Объект класса SharedPreferences для сохранения данных. */
    private SharedPreferences preferences;

    PlayerStorage(Context context){
        /* Загрузка сохранения с ключем hacked_player. */
        preferences = context.getSharedPreferences(GameConfig.keyStoragePlayer, Context.MODE_PRIVATE);
    }

    /** Метод сохрания иконки игрока. */
    void saveIcon(int icon){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playerIcon", icon);
        editor.apply();
    }

    /** Метод сохрания имени игрока. */
    void saveName(String name){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("playerName", name);
        editor.apply();
    }

    /** Метод сохрания опыта игрока. */
    void saveExperience(int experience){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playerExperience", experience);
        editor.apply();
    }

    /** Метод сохрания уровня игрока. */
    void saveLevel(int level){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playerLevel", level);
        editor.apply();
    }

    /** Метод сохрания количества денег игрока. */
    void saveMoney(int money){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playerMoney", money);
        editor.apply();
    }

    /** Метод загрузки иконки игрока. */
    int loadIcon(){
        return preferences.getInt("playerIcon", 0);
    }

    /** Метод загрузки имени игрока. */
    String loadName(){
        return preferences.getString("playerName", "Player");
    }

    /** Метод загрузки опыта игрока. */
    int loadExperience(){
        return preferences.getInt("playerExperience", 0);
    }

    /** Метод загрузки уровня игрока. */
    int loadLevel(){
        return preferences.getInt("playerLevel", 1);
    }

    /** Метод загрузки количества денег игрока. */
    int loadMoney(){
        return preferences.getInt("playerMoney", 0);
    }
}
