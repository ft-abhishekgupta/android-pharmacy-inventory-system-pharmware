package com.android.pharmware;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class AutoAdapter extends ArrayAdapter<Model> {

    Context context;
    int resource, textViewResourceId;
    ArrayList<Model> items, tempItems, suggestions;
    String item = "", mrp = "", unit = "", expdate = "", batch = "", key = "", oqty, ecode = "";

    public AutoAdapter(Context context, int resource, int textViewResourceId, ArrayList<Model> items) {

        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        this.tempItems = (ArrayList<Model>) items.clone();
        this.suggestions = new ArrayList<Model>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autotext_list, parent, false);
        }
        final Model model = items.get(position);
        if (model != null) {

            TextView item = (TextView) view.findViewById(R.id.item_name);

            if (item != null)

                item.setText(model.getEiname() + " (" + model.getEcode() + ")");
        }
        return view;
    }

    @Override
    public Filter getFilter() {

        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Model) resultValue).getEiname() + " (" + ((Model) resultValue).getEcode() + ")";
            item = ((Model) resultValue).getEiname();
            mrp = ((Model) resultValue).getEmrp();
            expdate = ((Model) resultValue).getEexpdate();
            batch = ((Model) resultValue).getEbatchNo();
            key = ((Model) resultValue).getItemId();
            oqty = ((Model) resultValue).getEqty();
            unit = ((Model) resultValue).getEunit();
            ecode = ((Model) resultValue).getEcode();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Model model : tempItems) {
                    if (model.getEiname().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(model);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Model> filterList = (ArrayList<Model>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Model model : filterList) {
                    add(model);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public String[] det() {
        String[] str = { item, mrp, batch, expdate, key, oqty, unit, ecode };
        return str;
    }
}
