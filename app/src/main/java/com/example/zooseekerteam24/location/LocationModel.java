package com.example.zooseekerteam24.location;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LocationModel extends AndroidViewModel {
    private final String TAG = "LocationModel";

    // live data which we can manually update the value for using `postValue`
    private LiveData<Coord> locationProviderSource = null;

    // live data which we can manually update the value for using `postValue`
    private MutableLiveData<Coord> mockSource = null;

    // merges events from both of the other two LiveData
    private final MediatorLiveData<Coord> lastKnownCoords;

    public LocationModel(@NonNull Application application) {
        super(application);
        lastKnownCoords = new MediatorLiveData<>();

        // Create and add the mock source.
        mockSource = new MutableLiveData<>();
        lastKnownCoords.addSource(mockSource, lastKnownCoords::setValue);
    }

    public LiveData<Coord> getLastKnownCoords() {
        return lastKnownCoords;
    }

    public void setLastKnownCoords(Coord coords) {
        lastKnownCoords.setValue(coords);
    }

    /**
     * @param locationManager the location manager to request updates from.
     * @param provider        the provider to use for location updates (usually GPS).
     * @apiNote This method should only be called after location permissions have been obtained.
     * @implNote If a location provider source already exists, it is removed.
     */
    @SuppressLint("MissingPermission")
    public void addLocationProviderSource(LocationManager locationManager, String provider) {
        // If a location provider source is already added, remove it.
        if (locationProviderSource != null) {
            removeLocationProviderSource();
        }

        // Create a new GPS source.
        var providerSource = new MutableLiveData<Coord>();
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                var coord = Coord.fromLocation(location);
                Log.i(TAG, String.format("Model received GPS location update: %s", coord));
                providerSource.postValue(coord);
            }
        };
        // Register for updates.
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);

        locationProviderSource = providerSource;
        lastKnownCoords.addSource(locationProviderSource, lastKnownCoords::setValue);
    }

    void removeLocationProviderSource() {
        if (locationProviderSource == null) return;
        lastKnownCoords.removeSource(locationProviderSource);
    }

    @VisibleForTesting
    public void mockLocation(Coord coords) {
        Log.i(TAG, String.format("Model mockLocation: %s", coords));
        mockSource.postValue(coords);
        Log.d(TAG, "mockLocation: " + lastKnownCoords.getValue());
    }

    @VisibleForTesting
    public Future<?> mockRoute(List<Coord> route, long delay, TimeUnit unit) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            int i = 1;
            int n = route.size();
            for (var coord : route) {
                // Mock the location...
                Log.i(TAG, String.format("Model mocking route (%d / %d): %s", i++, n, coord));
                mockLocation(coord);

                // Sleep for a while...
                try {
                    Thread.sleep(unit.toMillis(delay));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

