package com.twoengers.mear.overpass_api;

import com.twoengers.mear.config.GameConfig;
import com.twoengers.mear.geodesy.Geodesy;
import com.twoengers.mear.places.Place;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

/** Класс для формирования строки запроса к серверу Overpass и парсинга скаченных данных. */
class OverpassHelper {
    /** Список объектов культуры. */
    private final String[] PLACE_CULTURE = {
            "amenity=arts_centre",
            "amenity=cinema",
            "amenity=theatre",
            "leisure=bandstand",
            "leisure=dance",
            "leisure=escape_game"
    };
    /** Список гражданских объектов. */
    private final String[] PLACE_CIVIL = {
            "amenity=courthouse",
            "amenity=embassy",
            "amenity=fire_station",
            "amenity=police",
            "amenity=post_office"
    };
    /** Список медецинских объектов. */
    private final String[] PLACE_MEDICINE = {
            "building=hospital",
            "amenity=clinic",
            "amenity=dentist",
            "amenity=doctors",
            "amenity=hospital",
            "amenity=pharmacy"
    };
    /** Список транспортных объектов. */
    private final String[] PLACE_TRANSPORT = {
            "amenity=bus_station",
            "amenity=fuel",
            "building=train_station"
    };
    /** Список коммерческих объектов. */
    private final String[] PLACE_BANK = {
            "building=commercial",
            "building=industrial",
            "amenity=atm",
            "amenity=bank",
            "amenity=bureau_de_change"
    };
    /** Список образовательных объектов. */
    private final String[] PLACE_EDUCATION = {
            "building=school",
            "building=university",
            "amenity=college",
            "amenity=school",
            "amenity=university",
            "amenity=library"
    };
    /** Список торговых объектов. */
    private final String[] PLACE_STORE = {
            "shop=convenience",
            "shop=department_store",
            "shop=general",
            "shop=kiosk",
            "shop=mall",
            "shop=supermarket",
            "shop=clothes",
            "shop=fabric",
            "shop=jewelry",
            "shop=second_hand",
            "shop=beauty",
            "shop=chemist",
            "shop=cosmetics",
            "shop=erotic",
            "shop=hairdresser",
            "shop=optician",
            "shop=perfumery",
            "shop=tattoo",
            "shop=computer"
    };

    /**
     * Метод получения URL для загрузки OSM-данных.
     * Входные параметры:
     *      (double) latitude - широта центра области загрузки;
     *      (double) longitude - долгота центра области загрузки;
     *      (String[]) places - список типов мест.
     * Возвращает:
     *      (byte) type - тип загружаемых объектов.
     */
    String getUrlOSM(double latitude, double longitude, byte type){
        /* Массив OSM-ключей с определённым типом. */
        String[] places = type == 0 ? PLACE_CULTURE :
                type == 1 ? PLACE_CIVIL :
                type == 2 ? PLACE_MEDICINE :
                type == 3 ? PLACE_TRANSPORT :
                type == 4 ? PLACE_BANK :
                type == 5 ? PLACE_EDUCATION :
                PLACE_STORE;

        /* Получаем широту и долготу нижней левой точки прямоугольной области загрузки. */
        double[] coordinatesMinPoint = Geodesy.coordinates(latitude, longitude,
                GameConfig.distanceDownloading, 225);
        /* Получаем широту и долготу правой верхней точки прямоугольной области загрузки. */
        double[] coordinatesMaxPoint = Geodesy.coordinates(latitude, longitude,
                GameConfig.distanceDownloading, 45);

        /* Начало строки запроса. */
        final String OSM_URL_START = "(";
        /* Окончание строки запроса. */
        final String OSM_URL_END = ");out center skel qt;";

        /* StringBuilder содержащий строку запроса. */
        StringBuilder OSM_URL = new StringBuilder(OSM_URL_START);

        /* Цикл добавления в строку запроса отдельных точек (nodes). */
        for (String place : places) {
            OSM_URL.append("node(")
                    .append(coordinatesMinPoint[0]).append(",")
                    .append(coordinatesMinPoint[1]).append(",")
                    .append(coordinatesMaxPoint[0]).append(",")
                    .append(coordinatesMaxPoint[1]).append(")[")
                    .append(place).append("];");
        }

        /* Цикл добавления в строку запроса областей (ways). */
        for (String place : places) {
            OSM_URL.append("way(")
                    .append(coordinatesMinPoint[0]).append(",")
                    .append(coordinatesMinPoint[1]).append(",")
                    .append(coordinatesMaxPoint[0]).append(",")
                    .append(coordinatesMaxPoint[1]).append(")[")
                    .append(place).append("];");
        }

        /* Добавление окончания строки запроса. */
        OSM_URL.append(OSM_URL_END);

        return OSM_URL.toString();
    }

    /**
     * Метод получения списка объектов из XML-строки.
     * Входные параметры:
     *      (String) stringXML - строка XML с объектами;
     *      (byte) type - тип объектов.
     * Возвращает:
     *      (ArrayList<>) places - список объектов.
     */
    ArrayList<Place> parseXML(String stringXML, byte type){

        /* Список с местами. */
        ArrayList<Place> places = new ArrayList<>();

        try {
            /* Получаем XML из строки. */
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(stringXML));

            /* ID точки. */
            String id = "";
            /* Широта точки. */
            float latitude;
            /* Долгота точки. */
            float longitude;

            /* Парсим, пока не дойдём до конца документа. */
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                /* Если текущий тег - точка (node). */
                if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals("node")) {
                    /* Получаем ID, широту и долготу места. */
                    id = parser.getAttributeValue(0);
                    latitude = Float.valueOf(parser.getAttributeValue(1));
                    longitude = Float.valueOf(parser.getAttributeValue(2));

                    /* Добавляем новое место в список. */
                    places.add(new Place(type, id, latitude, longitude));
                }
                /* Если текущий тег - область (way). */
                else if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals("way")){
                    /* Получаем ID места. */
                    id = parser.getAttributeValue(0);
                }
                /* Если текущий тег - значение центра области (center). */
                else if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals("center")){
                    /* Получаем широту и долготу места. */
                    latitude = Float.valueOf(parser.getAttributeValue(0));
                    longitude = Float.valueOf(parser.getAttributeValue(1));

                    /* Добавляем новое место в список. */
                    places.add(new Place(type, id, latitude, longitude));
                }

                /* Переходим к следущему тегу. */
                parser.next();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        /* Возвращаем список мест. */
        return places;
    }
}
