package com.twoengers.mear.places;

/** Класс описывающий объект карты. */
public class Place {
    /** Тип объекта. */
    private byte type;
    /** ID объекта. */
    private String id;
    /** Широта объекта. */
    private float latitude;
    /** Долгота объекта. */
    private float longitude;
    /** Состояние объекта (взломан ли). */
    private boolean isHacked;
    /** Временная метка взлома. */
    private long timestamp;

    public Place(byte type, String id, float latitude, float longitude){
        this.type = type;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHacked = false;
        this.timestamp = 0;
    }

    public Place(byte type, String id, float latitude, float longitude, boolean isHacked, long timestamp){
        this.type = type;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHacked = isHacked;
        this.timestamp = timestamp;
    }

    /* ТИП ОБЪЕКТА. */
    /** Метод получения типа объекта. */
    public byte getType() {
        return type;
    }

    /** Метод установки типа объекта. */
    public void setType(byte type) {
        this.type = type;
    }

    /* ID ОБЪЕКТА. */
    /** Метод получения ID объекта. */
    public String getId() {
        return id;
    }

    /** Метод установки ID объекта. */
    public void setId(String id) {
        this.id = id;
    }

    /* ШИРОТА ОБЪЕКТА. */
    /** Метод получения широты объекта. */
    public float getLatitude() {
        return latitude;
    }

    /** Метод установки широты объекта. */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /* ДОЛГОТА ОБЪЕКТА. */
    /** Метод получения долготы объекта. */
    public float getLongitude() {
        return longitude;
    }

    /** Метод установки долготы объекта. */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /* СОСТОЯНИЕ ОБЪЕКТА. */
    /** Метод, проверяющий взломан ли объект. */
    public boolean isHacked() {
        return isHacked;
    }

    /** Метод устанавливающий взлом объекта. */
    public void setHacked(boolean hacked) {
        isHacked = hacked;
    }

    /* ВРЕМЕННАЯ МЕТКА ВЗЛОМА ОБЪЕКТА. */
    /** Метод возвращающий временную метку взлома объекта (в секундах). */
    long getTimestamp() {
        return timestamp;
    }

    /** Метод устанавливающий временную метку взлома объекта (в секундах). */
    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
