package com.example.petr.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> saggestions;
    private Context context;

    private native String[] yandexSuggestAPI(String requestPart);

    public SearchAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public int getCount() {
        return saggestions.size();
    }

    @Override
    public String getItem(int index) {
        return saggestions.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        }
        TextView v = (TextView) convertView;
        v.setText(getItem(position));
        v.setBackgroundColor(Color.parseColor(Config.SUGGESTION_BACKGROUND_COLOR));
        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();

                if (charSequence != null) {

                    List<String> results = findSuggestions(charSequence.toString());
                    filterResults.values = results;
                    filterResults.count = results.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                if (filterResults != null && filterResults.count > 0) {
                    saggestions = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };


        return filter;
    }


    private List<String> findSuggestions(String suggestionPart) {
        // using native method
        String[] a = yandexSuggestAPI(suggestionPart);

        List<String> res = new ArrayList<String>(Arrays.asList(a));

        return res;
    }

    static {
        System.loadLibrary("yandexAPI");
    }
}
