package com.gobicloo.util;

import com.gobicloo.object.Station;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter for getting stations from json
 */
public class Converter {

    /**
     * Parse json to an array list
     * @param json
     * @return
     */
    public static ArrayList<Station> parseJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Station>>() {}.getType();

        ArrayList<Station> stations = gson.fromJson(json, type);

        return stations;
    }
}
