package com.example.zooseekerteam24.location;

import android.content.Context;

import com.example.zooseekerteam24.ZooData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Coords {
    public static final Coord UCSD = Coord.of(32.8801, -117.2340);
    public static final Coord ZOO = Coord.of(32.7353, -117.1490);

    /**
     * @param p1 first coordinate
     * @param p2 second coordinate
     * @return midpoint between p1 and p2
     */
    public static Coord midpoint(Coord p1, Coord p2) {
        return Coord.of((p1.lat + p2.lat) / 2, (p1.lng + p2.lng) / 2);
    }

    public static double calculateDistance(Coord p1, Coord p2){
        var dLat = p1.lat - p2.lat;
        var dLng = p1.lng - p2.lng;
        return Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLng, 2));
    }

    public static List<Coord> loadListOfCoordsFromJSON(Context context, String content){
        List<Coord> coords = Collections.emptyList();
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        InputStreamReader reader = new InputStreamReader(inputStream);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Coord>>(){}.getType();
        coords = gson.fromJson(reader, listType);
        return coords;
    }

    /**
     * @param p1 start coordinate
     * @param p2 end coordinate
     * @param n number of points between to interpolate.
     * @return a list of evenly spaced points between p1 and p2 (including p1 and p2).
     */
    public static Stream<Coord> interpolate(Coord p1, Coord p2, int n) {
        // Map from i={0, 1, ... n} to t={0.0, 0.1, ..., 1.0} with n divisions.
        ///     t(i; n) = i / n
        // Linear interpolate between l1 and l2 using t:
        //      p(t) = p1 + (p2 - p1) * t

        return IntStream.range(0, n+1)
                .mapToDouble(i -> (double) i / (double) n)
                .mapToObj(t -> Coord.of(
                        p1.lat + (p2.lat - p1.lat) * t,
                        p1.lng + (p2.lng - p1.lng) * t
                ));
    }
}