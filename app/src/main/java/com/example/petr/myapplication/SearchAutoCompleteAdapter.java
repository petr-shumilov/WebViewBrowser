package com.example.petr.myapplication;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by petr on 28.06.17.
 */


public class SearchAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private native String[] yandexSuggestAPI(String requestPart);
    private List<String> mResults;
    private Context mContext;

    public SearchAutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public String getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        }
        TextView v = (TextView) convertView;
        v.setText(getItem(position));
        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();

                List<String> results = findSuggestions(charSequence.toString());

                if (charSequence.length() != 0) {
                    filterResults.values = results;
                    filterResults.count = results.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                if (filterResults != null && filterResults.count > 0) {
                    mResults = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };


        return filter;
    }


    private List<String> findSuggestions(String suggestionPart) {

        String[] a = yandexSuggestAPI(suggestionPart);

        List<String> res = new ArrayList<String>(Arrays.asList(a));

        return res;
    }
}
