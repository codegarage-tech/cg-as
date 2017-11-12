package com.reversecoder.mh.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.reversecoder.mh.R;
import com.reversecoder.mh.model.City;

import java.util.ArrayList;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class StateListAdapter<T> extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<T> mData;
    private static LayoutInflater inflater = null;

    public StateListAdapter(Activity activity) {
        mActivity = activity;
        mData = new ArrayList<T>();
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<T> getData() {
        return mData;
    }

    public void setData(ArrayList<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public int getItemPosition(String name) {
        for (int i = 0; i < mData.size(); i++) {
            City city = (City) mData.get(i);
            if (city.getName().contains(name)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row_state, null);

        final City city = (City) getItem(position);

        TextView names = (TextView) vi.findViewById(R.id.tv_item_name);
        final RadioButton radioButton = (RadioButton) vi.findViewById(R.id.rb_city);
        radioButton.setChecked(city.isChecked());
        radioButton.setVisibility(city.isChecked() ? View.VISIBLE : View.GONE);

        names.setText(city.getName());

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSelection(city, (city.isChecked() ? false : true));
            }
        });

        return vi;
    }

    private void updateSelection(City city, boolean isChecked) {
        for (T mCity : mData) {
            City cityData = (City) mCity;
            if (cityData.getName().equalsIgnoreCase(city.getName())) {
                cityData.setChecked(isChecked);
            } else {
                cityData.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isAnyItemSelected() {
        for (T mCity : mData) {
            City cityData = (City) mCity;
            if (cityData.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public T getSelectedItem() {
        for (T mCity : mData) {
            City cityData = (City) mCity;
            if (cityData.isChecked()) {
                return (T) cityData;
            }
        }
        return null;
    }
}