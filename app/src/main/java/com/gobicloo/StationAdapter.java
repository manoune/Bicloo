package com.gobicloo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gobicloo.object.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Station Adapter for ListView
 */
public class StationAdapter extends ArrayAdapter<Station> implements Filterable {

    private List<Station> stationsFull;

    public StationAdapter(Context context, List<Station> stations) {
        super(context, 0, stations);
        this.stationsFull = stations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_station,parent, false);
        }

        // Get view
        StationViewHolder viewHolder = (StationViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new StationViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.station);
            viewHolder.availableBikes = (TextView) convertView.findViewById(R.id.availableStands);
            viewHolder.availableStands = (TextView) convertView.findViewById(R.id.availableBikes);
            convertView.setTag(viewHolder);
        }

        Station station = getItem(position);

        // Fill view
        viewHolder.name.setText(station.getNameForDisplay());
        viewHolder.availableBikes.setText(String.valueOf(station.getAvailable_bikes() + " Bicloo"));
        viewHolder.availableStands.setText(String.valueOf(station.getAvailable_bike_stands() + " places restantes"));

        // Set color for bike availability
        if(station.getAvailable_bikes() == 0) {
            viewHolder.availableBikes.setTextColor(Color.RED);
        } else {
            viewHolder.availableBikes.setTextColor(Color.parseColor("#ff669900"));
        }
        // Set color for bike stands
        if(station.getAvailable_bike_stands() == 0) {
            viewHolder.availableStands.setTextColor(Color.RED);
        } else {
            viewHolder.availableStands.setTextColor(Color.parseColor("#ff669900"));
        }

        return convertView;
    }

    private class StationViewHolder {
        public TextView name;
        public TextView availableStands;
        public TextView availableBikes;
    }
	
	@Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                stationsFull = (List<Station>) results.values;
                // TODO To fix
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Station> filteredArrayNames = new ArrayList<Station>();

				if (constraint.toString().equals("BIKE_ALL")) {
					filteredArrayNames = new ArrayList<Station>(stationsFull);
				} else {
					// Perfom filtering
					for (Station station : stationsFull) {
						// status OPEN ou CLOSE
						if (constraint.toString().equals("OPEN") && station.getStatus().startsWith(constraint.toString()))  {
							filteredArrayNames.add(station);
						}
						// ou available_bikes
						else if (constraint.toString().equals("BIKE_AVAILABLE") && station.getAvailable_bike_stands() > 0) {
							filteredArrayNames.add(station);
						}
					}
				}

                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }
}
