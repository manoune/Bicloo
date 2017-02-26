package com.gobicloo;

import android.os.AsyncTask;

import com.gobicloo.object.Station;
import com.gobicloo.util.Converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Download Task to get stations
 */
public class DownloadTask extends AsyncTask<Void, Station, List<Station>> {
    private MainActivity mainActivity;
    private StationListFragment stationListFragment;

    public DownloadTask(MainActivity main, StationListFragment list) {
        this.mainActivity = main;
        this.stationListFragment = list;
    }

    @Override
    protected List<Station> doInBackground(Void... params) {
        final StringBuilder json = new StringBuilder();
        HttpURLConnection conn = null;
        String urlStr = "https://api.jcdecaux.com/vls/v1/stations?contract=Nantes&apiKey=";
        List<Station> stations = new ArrayList<Station>();

        // Get stations from URL
        String fullUrl = urlStr + mainActivity.getResources().getString(R.string.jcdecaux_key);
        URL url = null;
        try {
            url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Read the JSON data into the StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
            // Parse json got to list of stations (object)
            stations = Converter.parseJson(json.toString());

            for (Station station : stations) {
                // Publish progress
                publishProgress(station);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stations;
    }

    @Override
    protected void onProgressUpdate(Station... station) {
        // Fill the list in ListView
        stationListFragment.getStations().add(station[0]);
        stationListFragment.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(List<Station> stations) {
        super.onPostExecute(stations);
        // When finished, set list in MainActivity
        mainActivity.setStations(stations);
    }
}
