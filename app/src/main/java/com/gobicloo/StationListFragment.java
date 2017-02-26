package com.gobicloo;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gobicloo.object.Position;
import com.gobicloo.object.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Station list Fragment
 */
public class StationListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    ListView mListView;
    List<Station> stations;
    StationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);

        stations = new ArrayList<Station>();
        adapter = new StationAdapter(getContext(), stations);
        mListView.setAdapter(adapter);
    }

    public List<Station> getStations() {
        return stations;
    }

    public StationAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //Locate station on map fragment
        Position stationPosition = stations.get(position).getPosition();
        ((MainActivity) this.getActivity()).locateStationOnMap(stationPosition.getLat(), stationPosition.getLng());
        ((MainActivity) this.getActivity()).setBottomSheetForStation(stations.get(position));
        ((MainActivity) this.getActivity()).getmBottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
