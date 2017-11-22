package com.reversecoder.mh.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.reversecoder.mh.R;
import com.reversecoder.mh.model.Music;

import java.util.ArrayList;

import static com.reversecoder.mh.util.AppUtils.getUnderlinedText;

/**
 * @author Md. Rashadul Alam
 */
public class SearchListViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Music> mData;
    private static LayoutInflater inflater = null;
    private String TAG = SearchListViewAdapter.class.getSimpleName();

    public SearchListViewAdapter(Activity activity) {
        mActivity = activity;
        mData = new ArrayList<Music>();
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<Music> getData() {
        return mData;
    }

    public void setData(ArrayList<Music> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Music getItem(int position) {
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
            vi = inflater.inflate(R.layout.list_row_music, null);

        final Music mMusicFile = getItem(position);

        TextView musicName = (TextView) vi.findViewById(R.id.tv_music_name);
        TextView musicDescription = (TextView) vi.findViewById(R.id.tv_music_description);
        TextView musicSpentTime = (TextView) vi.findViewById(R.id.tv_spent_time);
        TextView musicFreePaid = (TextView) vi.findViewById(R.id.tv_music_free_paid);
        ImageView musicPlayStop = (ImageView) vi.findViewById(R.id.iv_music_play_stop);
        ImageView musicEqualizer = (ImageView) vi.findViewById(R.id.iv_equalizer);
        CircularProgressBar circularProgressBar = (CircularProgressBar) vi.findViewById(R.id.cp_streaming_music);

        musicName.setText(mMusicFile.getMusic_title());
        musicDescription.setText(getUnderlinedText(mMusicFile.getFirst_name() + " " + mMusicFile.getLast_name()));

        return vi;
    }
}