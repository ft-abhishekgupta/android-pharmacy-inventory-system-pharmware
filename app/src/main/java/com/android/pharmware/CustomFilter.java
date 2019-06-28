package com.android.pharmware;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by corei3 on 09-07-2017.
 */

public class CustomFilter extends Filter {

    MyAdapter adapter;
    ArrayList<Model> filterList;

    public CustomFilter(ArrayList<Model> filterList, MyAdapter adapter) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            // CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            // STORE OUR FILTERED PLAYERS
            ArrayList<Model> filteredPlayers = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                // CHECK
                if (filterList.get(i).getEiname().toUpperCase().contains(constraint)) {
                    // ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }
            results.count = filteredPlayers.size();
            results.values = filteredPlayers;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.models = (ArrayList<Model>) results.values;
        // REFRESH
        adapter.notifyDataSetChanged();
    }
}
