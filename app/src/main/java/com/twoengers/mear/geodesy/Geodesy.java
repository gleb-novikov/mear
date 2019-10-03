package com.twoengers.mear.geodesy;

/** Класс для геодезических рассчётов. */
public class Geodesy {
    /** Длина 1 градуса меридиана (в метрах). */
    private static final int LENGTH_LON = 111135;
    /** Радиус Земли (в метрах). */
    private static final int RADIUS_EARTH = 6371000;

    /**
     * Метод вычесления растояния между двумя объектами.
     * Входные параметры:
     *      (double) lat1, lon1 - широта и долгота первого объекта;
     *      (double) lat2, lon2 - широта и долгота второго объекта.
     * Возвращаемое значение:
     *      (double) distance - растояние в метрах.
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2){
        /* Преобразование координат в радианную меру. */
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        /* Вычисление ортодромии в радианной мере. */
        double orthodromicRad = Math.acos(
                Math.sin(lat1Rad) * Math.sin(lat2Rad) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(lon2Rad - lon1Rad));

        /* Преобразование ортодромии в градусную меру. */
        double orthodromicDeg = Math.toDegrees(orthodromicRad);

        /* Вычисление длины ортодромии в метрах. */
        return orthodromicDeg * LENGTH_LON;
    }

    /**
     * Метод вычисления угла между двумя объектами.
     * Входные параметры:
     *      (double) lat1, lon1 - широта и долгота первого объекта;
     *      (double) lat2, lon2 - широта и долгота второго объекта;
     * Возвращаемое значение:
     *      (double) angle - угол в градусах.
     */
    public static double angle(double lat1, double lon1, double lat2, double lon2){
        /* Преобразование координат в радианную меру. */
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        /* Тайные знания. */
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(lon2Rad - lon1Rad);
        double y = Math.sin(lon2Rad - lon1Rad) * Math.cos(lat2Rad);
        double z = x < 0 ? Math.atan(-y / x) + Math.PI : Math.atan(-y / x);
        double c = (z + Math.PI) % (2 * Math.PI) - Math.PI;

        /* Вычисление радианной меры угла. */
        double angleRad = 2 * Math.PI + 2 * Math.PI * Math.floor(c / (2 * Math.PI)) - c;

        /* Преобразование угла в градусную меру. */
        return Math.toDegrees(angleRad);
    }

    /**
     * Метод вычисления координат объекта, расположенного на определённом расстоянии и
     * под нужным углом, относительно входных координат объекта.
     * На вход:
     *      (double) lat - широта объекта отсчёта;
     *      (double) lon - долгота объекта отсчёта;
     *      (double) distance - расстояние в метрах;
     *      (double) angle - направление относительно входного объета (в градусах).
     * Возвращаемое значение:
     *      (double[2]) latlon - массив координат нужного объекта.
     */
    public static double[] coordinates(double lat, double lon, double distance, double angle){
        /* Преобразование координат и угла в радианную меру. */
        double latRad = Math.toRadians(lat);
        double lonRad = Math.toRadians(lon);
        double angleRad = Math.toRadians(angle);

        /* Вычисление долготы и широты объекта в радианной мере. */
        double objectLatRad = Math.asin(
                Math.sin(latRad) * Math.cos(distance / RADIUS_EARTH) +
                Math.cos(latRad) * Math.sin(distance / RADIUS_EARTH) * Math.cos(angleRad));
        double objectLonRad = lonRad + Math.atan2(
                Math.sin(angleRad) * Math.sin(distance / RADIUS_EARTH) * Math.cos(latRad),
                Math.cos(distance / RADIUS_EARTH) - Math.sin(latRad) * Math.sin(latRad));

        /* Преобразование координат в градусную меру. */
        double objectLatDeg = Math.toDegrees(objectLatRad);
        double objectLonDeg = Math.toDegrees(objectLonRad);

        /* Массив с координатами нужного объекта. */
        return new double[]{objectLatDeg, objectLonDeg};
    }
}
