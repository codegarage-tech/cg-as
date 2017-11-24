package com.reversecoder.mh.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reversecoder.mh.R;
import com.reversecoder.mh.model.City;
import com.reversecoder.mh.model.Country;
import com.reversecoder.mh.model.MusicCategory;
import com.reversecoder.mh.model.TimeZone;

import java.util.ArrayList;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class CommonSpinnerAdapter<T> extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<T> mData;
    private static LayoutInflater inflater = null;
    private ADAPTER_TYPE mAdapterType;

    public enum ADAPTER_TYPE {COUNTRY, TIME_ZONE, CITY, MUSIC_CATEGORY}

    public CommonSpinnerAdapter(Activity activity, ADAPTER_TYPE adapterType) {
        mActivity = activity;
        mAdapterType = adapterType;
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
            if (mAdapterType == ADAPTER_TYPE.COUNTRY) {
                Country country = (Country) mData.get(i);
                if (country.getName().contains(name)) {
                    return i;
                }
            } else if (mAdapterType == ADAPTER_TYPE.TIME_ZONE) {
                TimeZone timeZone = (TimeZone) mData.get(i);
                if (timeZone.getName().contains(name)) {
                    return i;
                }
            } else if (mAdapterType == ADAPTER_TYPE.CITY) {
                City city = (City) mData.get(i);
                if (city.getName().contains(name)) {
                    return i;
                }
            } else if (mAdapterType == ADAPTER_TYPE.MUSIC_CATEGORY) {
                MusicCategory mItem = (MusicCategory) mData.get(i);
                if (mItem.getName().contains(name)) {
                    return i;
                }
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
            vi = inflater.inflate(R.layout.spinner_row_user, null);

        TextView names = (TextView) vi.findViewById(R.id.tv_item_name);

        if (mAdapterType == ADAPTER_TYPE.COUNTRY) {
            Country country = (Country) getItem(position);
            names.setText(country.getName());
        } else if (mAdapterType == ADAPTER_TYPE.TIME_ZONE) {
            TimeZone timeZone = (TimeZone) getItem(position);
            names.setText(timeZone.getName());
        } else if (mAdapterType == ADAPTER_TYPE.CITY) {
            City city = (City) getItem(position);
            names.setText(city.getName());
        } else if (mAdapterType == ADAPTER_TYPE.MUSIC_CATEGORY) {
            MusicCategory mItem = (MusicCategory) getItem(position);
            names.setText(mItem.getName());
        }


        return vi;
    }
}