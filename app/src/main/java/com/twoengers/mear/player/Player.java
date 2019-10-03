package com.twoengers.mear.player;

import android.content.Context;

import com.twoengers.mear.R;

/** Класс, описывающий параметры игрока. */
public class Player {
    /** Иконка игрока. */
    private static int icon;
    /** Имя игрока. */
    private static String name;
    /** Опыт игрока. */
    private static int experience;
    /** Уровень игрока. */
    private static int level;
    /** Количество долларов игрока. */
    private static int money;
    /** Широта игрока. */
    private static double latitude;
    /** Долгота игрока. */
    private static double longitude;
    /** Объект для сохранения и загрузки данных игрока. */
    private static PlayerStorage playerStorage;

    /** Метод загрузки данных игрока из кэша. */
    public static void loadFromStorage(Context context){
        /* Загрузка сохранённых параметров игрока. */
        playerStorage = new PlayerStorage(context);

        icon = playerStorage.loadIcon();
        name = playerStorage.loadName();
        experience = playerStorage.loadExperience();
        level = playerStorage.loadLevel();
        money = playerStorage.loadMoney();
    }

    /** Метод установки текущего контекста для доступа к хранилищу. */
    public static void setContext(Context context){
        playerStorage = new PlayerStorage(context);
    }

    /** Метод получения иконки игрока. */
    public static int getIcon(){
        return icon;
    }

    /** Метод установки иконки игрока. */
    public static void setIcon(int icon) {
        Player.icon = icon;
        /* Сохранение иконки игрока. */
        playerStorage.saveIcon(Player.icon);
    }

    /** Метод получения картинки игрока из ресурсов проекта. */
    public static int getIconResource(){
        int resource = 0;

        switch (icon){
            case 0:
                resource = R.drawable.icon_player_00;
                break;
            case 1:
                resource = R.drawable.icon_player_01;
                break;
            case 2:
                resource = R.drawable.icon_player_02;
                break;
        }

        return resource;
    }

    /** Метод получения имени игрока. */
    public static String getName() {
        return name;
    }

    /** Метод установки имени игрока. */
    public static void setName(String name) {
        Player.name = name;
        /* Сохранение имени игрока. */
        playerStorage.saveName(Player.name);
    }

    /** Метод получения опыта игрока. */
    public static int getExperience() {
        return experience;
    }

//    /** Метод установки опыта игрока. */
//    public static void setExperience(int experience) {
//        Player.experience = experience;
//        /* Сохранение опыта игрока. */
//        playerStorage.saveExperience(Player.experience);
//    }

    /** Метод добавления очков опыта игрока. */
    public static void addExperience(int experience) {
        Player.experience += experience;

        /* Если доступно повышение уровня, повысить его. */
        if (isLevelUp()) addLevel();

        /* Сохранение опыта игрока. */
        playerStorage.saveExperience(Player.experience);
    }

    /** Метод получения уровня игрока. */
    public static int getLevel() {
        return level;
    }

//    /** Метод установки уровня игрока. */
//    public static void setLevel(int level) {
//        Player.level = level;
//        /* Сохранение уровня игрока. */
//        playerStorage.saveLevel(Player.level);
//    }

    public static int getMaxExperience(){
        return (int) (100 * Math.pow(level, 2));
    }

    /** Метод, проверяющий набран ли опыт для повышения уровня. */
    private static boolean isLevelUp(){
        return experience >= getMaxExperience();
    }

    /** Метод повышения уровня. */
    private static void addLevel(){
        /* Вычитание опыта и увеличение уровня. */
        experience -= getMaxExperience();
        level++;

        /* Если после этого, опять доступно повышение уровня, то повысить его. */
        if (isLevelUp()) addLevel();

        /* Сохранение уровня игрока. */
        playerStorage.saveLevel(level);
    }

    /** Метод получения количества денег игрока. */
    public static int getMoney() {
        return money;
    }

//    /** Метод установки количества денег игрока. */
//    public static void setMoney(int money) {
//        Player.money = money;
//        /* Сохранение количества денег игрока. */
//        playerStorage.saveMoney(Player.money);
//    }

    /** Метод добавления денег. */
    public static void addMoney(int money) {
        Player.money += money;
        /* Сохранение количества денег игрока. */
        playerStorage.saveMoney(Player.money);
    }

    /** Метод вычитания денег. */
    public static void subtractMoney(int money) {
        Player.money -= money;
        /* Сохранение количества денег игрока. */
        playerStorage.saveMoney(Player.money);
    }

    /** Метод получения широты игрока. */
    public static double getLatitude() { return latitude; }

    /** Метод установки широты игрока. */
    public static void setLatitude(double latitude) {
        Player.latitude = latitude;
    }

    /** Метод получения долготы игрока. */
    public static double getLongitude() { return longitude; }

    /** Метод установки долготы игрока. */
    public static void setLongitude(double longitude) {
        Player.longitude = longitude;
    }
}
