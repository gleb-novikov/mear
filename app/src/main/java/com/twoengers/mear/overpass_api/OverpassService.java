package com.twoengers.mear.overpass_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** Интерфейс для загрузки OpenStreetMap данных через Retrofit 2. */
public interface OverpassService {
    @GET("/api/interpreter")
    Call<ResponseBody> getData(@Query("data") String data);
}
