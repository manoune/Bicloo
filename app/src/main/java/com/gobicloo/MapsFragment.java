package com.gobicloo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gobicloo.object.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.List;

/**
 * Maps Fragment
 */
public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private Location currentLocation;
    private MapView mMapView;
    private GoogleApiClient googleApiClient;

    private List<Station> stations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
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
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    // Show rationale and request permission.
                }

                // TODO Test fixed location
                //Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                //Location currentLocation = googleMap.getMyLocation();

                currentLocation = new Location("provider");
                currentLocation.setLatitude(47.213875);
                currentLocation.setLongitude(-1.549483);

                setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

                // Add markers on map
                try {
                    createMarkersForStations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }
    /**
     *  Update the map with a new location
     */
    public void setLocation(double lat, double longitude) {
        String latLongString = "";

        if ((lat == 0) && (longitude == 0)) {
            latLongString = "No location found";
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longitude), 16.0f));
        }

        Log.i("gmaps", latLongString);
    }

    /**
     * Add markers for stations
     * @throws JSONException
     */
    void createMarkersForStations() throws JSONException {
        stations = ((MainActivity) this.getActivity()).getStations();
        mMap.setOnMarkerClickListener(this);
        if(stations != null) {
            int index = 0;
            for (Station station : stations) {
                // Create a marker for each city in the JSON data.
                mMap.addMarker(new MarkerOptions()
                                .title(station.getNameForDisplay())
                                .snippet(String.valueOf(index))
                                .position(new LatLng(
                                        station.getPosition().getLat(),
                                        station.getPosition().getLng()
                                ))
                );
                index++;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Display bottom sheet
        Station station = stations.get(Integer.parseInt(marker.getSnippet()));
        ((MainActivity) this.getActivity()).setBottomSheetForStation(station);
        ((MainActivity) this.getActivity()).getmBottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        return false;
    }
}
