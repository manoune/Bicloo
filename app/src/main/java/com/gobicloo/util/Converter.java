package com.gobicloo.util;

import com.gobicloo.object.Station;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converter {
    public static List<Station> parseJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Station>>() {}.getType();

        //String json = gson.toJson(json, type);
        System.out.println(json);
        List<Station> stations = gson.fromJson(json, type);

        for (Station station : stations) {
            System.out.println(station);
        }
        return stations;
    }
}
