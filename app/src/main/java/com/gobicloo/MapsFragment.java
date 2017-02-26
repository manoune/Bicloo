package com.gobicloo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gobicloo.object.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private String url = "https://api.jcdecaux.com/vls/v1/stations?contract=Nantes&apiKey=";
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    MapView mMapView;

    private LocationManager locationManager;
    private String provider;
    private List<Station> stations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // For showing a move to my location button
                mMap.setMyLocationEnabled(true);

                setCurrentLocation();

                try {
                    createMarkersFromJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    /** Update the map with a new location */
    private void setCurrentLocation() {
        String latLongString = "";
        currentLocation = new Location("provider");
        currentLocation.setLatitude(47.213875);
        currentLocation.setLongitude(-1.549483);

        if ((currentLocation.getLatitude() == 0) && (currentLocation.getLongitude() == 0)) {
            latLongString = "No location found";

        } else {
            addPointOnMap(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 16.0f));
        }

        Log.i("gmaps", latLongString);
    }

    /*
     * Ajouter points vous_etes_ici ou station
     */
    public void addPointOnMap(Double latitude, Double longitude){
        /*mMap.addMarker(new MarkerOptions()
                .position(new LatLng(pt_lat, pt_lng))
                .title("test"));*/

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(10)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*private void setUpMap() {
        // Retrieve the city data from the web service
        // In a worker thread since it's a network operation.
        new Thread(new Runnable() {
            public void run() {
                try {
                    getStationsInNantes();
                } catch (IOException e) {
                    return;
                }
            }
        }).start();
    }*/

    /*protected void getStationsInNantes() throws IOException {
        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {
            // Connect to the web service
            String fullUrl = url + getResources().getString(R.string.jcdecaux_key);
            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Read the JSON data into the StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error connecting to service", e);
            throw new IOException("Error connecting to service", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Create markers for the city data.
        // Must run this on the UI thread since it's a UI operation.
        getContext().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    stations = Converter.parseJson(json.toString());
                    createMarkersFromJson(stations);
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Error processing JSON", e);
                }
            }
        });
    }*/

    void createMarkersFromJson() throws JSONException {
        stations = ((MainActivity) this.getActivity()).getStations();
        if(stations != null) {
            for (Station station : stations) {
                // Create a marker for each city in the JSON data.
            /*mMap.addMarker(new MarkerOptions()
                            .title(station.getName())
                                    //.snippet(Integer.toString(jsonObj.getInt("population")))
                            .position(new LatLng(
                                    station.getPosition().getLat(),
                                    station.getPosition().getLng()
                            ))
            );*/

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(station.getPosition().getLat(), station.getPosition().getLng()))
                        .radius(10)
                        .strokeColor(Color.parseColor("#4682B4"))
                        .fillColor(Color.parseColor("#4682B4")));
            }
        }
    }
}
