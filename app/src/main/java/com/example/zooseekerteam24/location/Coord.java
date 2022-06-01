package com.example.zooseekerteam24.location;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.zooseekerteam24.ZooData;

public class Coord {
    public Coord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public final double lat;
    public final double lng;
    public static final double latToFt = 363843.57;
    public static final double lngToFt = 307515.50;

    public static Coord of(double lat, double lng) {
        return new Coord(lat, lng);
    }

    public static Coord of(ZooData.Node node) {
        return new Coord(node.lat, node.lng);
    }


    public static Coord fromLocation(Location location) {
        return Coord.of(location.getLatitude(), location.getLongitude());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Double.compare(coord.lat, lat) == 0 && Double.compare(coord.lng, lng) == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Coord{lat=%s, lng=%s}", lat, lng);
    }

    public static double distFt(Coord c1 , Coord c2){
        var dLat = (c1.lat - c2.lat)* latToFt;
        var dLng = (c1.lng - c2.lng)*lngToFt;
        return Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLng, 2));
    }

    public static double dist(Coord c1 , Coord c2){
        var dLat = (c1.lat - c2.lat);
        var dLng = (c1.lng - c2.lng);
        return Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLng, 2));
    }
}

