package com.rc.abovesound.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.rc.abovesound.R;
import com.rc.abovesound.activity.OwnMusicListActivity;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.model.UserData;
import com.rc.abovesound.paypal.PayPalActivity;
import com.rc.abovesound.service.MediaService;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * @author Md. Rashadul Alam
 */
public class MusicListViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Music> mData;
    private static LayoutInflater inflater = null;
    private String TAG = MusicListViewAdapter.class.getSimpleName();
    private UserData user;

    private static class ViewHolder {
        TextView musicName;
        TextView musicDescription;
        TextView musicSpentTime;
        TextView musicFreePaid;
        ImageView musicPlayStop;
        ImageView musicEqualizer;
        CircularProgressBar circularProgressBar;

        Music music;
    }

    public MusicListViewAdapter(Activity activity) {
        mActivity = activity;
        mData = new ArrayList<Music>();
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(mActivity, AllConstants.SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(mActivity, AllConstants.SESSION_USER_DATA));
            user = UserData.getResponseObject(SessionManager.getStringSetting(mActivity, AllConstants.SESSION_USER_DATA), UserData.class);
        }
    }

    public ArrayList<Music> getData() {
        return mData;
    }

    public void setData(ArrayList<Music> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public int getItemPosition(Music music) {
        for (int i = 0; i < mData.size(); i++) {
            if ((mData.get(i).getPrimaryKey() == music.getPrimaryKey()
//                    && (mData.get(i).getFile_path().equalsIgnoreCase(music.getFile_path()))

            )) {
                Log.d(TAG, "getItemPosition==================================================");
                Log.d(TAG, "getItemPositionListview: " + mData.get(i).toString());
                Log.d(TAG, "getItemPositionUpdate: " + music.toString());
                Log.d(TAG, "getItemPosition==================================================");
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
    public Music getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getPrimaryKey();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View row = convertView;
        final Music mMusicFile = getItem(position);
        ViewHolder holder = null;

        if (row == null) {
            row = inflater.inflate(R.layout.list_row_music, parent, false);

            holder = new ViewHolder();

            holder.musicName = (TextView) row.findViewById(R.id.tv_music_name);
            holder.musicDescription = (TextView) row.findViewById(R.id.tv_music_description);
            holder.musicSpentTime = (TextView) row.findViewById(R.id.tv_spent_time);
            holder.musicFreePaid = (TextView) row.findViewById(R.id.tv_music_free_paid);
            holder.musicPlayStop = (ImageView) row.findViewById(R.id.iv_music_play_stop);
            holder.musicEqualizer = (ImageView) row.findViewById(R.id.iv_equalizer);
            holder.circularProgressBar = (CircularProgressBar) row.findViewById(R.id.cp_streaming_music);

            holder.music = mMusicFile;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();

            holder.music.setCircularProgressBar(null);
            holder.music = mMusicFile;
            holder.music.setCircularProgressBar(holder.circularProgressBar);
        }

//        TextView musicName = (TextView) row.findViewById(R.id.tv_music_name);
//        TextView musicDescription = (TextView) row.findViewById(R.id.tv_music_description);
//        TextView musicSpentTime = (TextView) row.findViewById(R.id.tv_spent_time);
//        TextView musicFreePaid = (TextView) row.findViewById(R.id.tv_music_free_paid);
//        ImageView musicPlayStop = (ImageView) row.findViewById(R.id.iv_music_play_stop);
//        ImageView musicEqualizer = (ImageView) row.findViewById(R.id.iv_equalizer);
//        CircularProgressBar circularProgressBar = (CircularProgressBar) row.findViewById(R.id.cp_streaming_music);

        holder.musicName.setText(mMusicFile.getMusic_title());
        holder.musicDescription.setText(AppUtils.getUnderlinedText(mMusicFile.getFirst_name() + " " + mMusicFile.getLast_name()));

        if (mMusicFile.getIsPlaying() == AllConstants.MEDIA_PLAYER_RUNNING) {
            Drawable stopDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_stop);
            holder.musicPlayStop.setImageDrawable(stopDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.PLAYING);
            holder.musicEqualizer.setImageDrawable(drawable);

            //to avoid arethmetic exception
            if (mMusicFile.getTotalTime() > 0) {
                Log.d(TAG, "total: " + mMusicFile.getTotalTime() + "");
                Log.d(TAG, "last progress: " + mMusicFile.getLastPlayed() + "");
                int progress = (int) (((float) mMusicFile.getLastPlayed() / mMusicFile.getTotalTime()) * 100);
                Log.d(TAG, "progress: " + progress + "");
                holder.circularProgressBar.setProgress(progress);
            }
            holder.musicSpentTime.setText(AppUtils.milliSecondsToTimer(mMusicFile.getLastPlayed()) + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
        } else if (mMusicFile.getIsPlaying() == AllConstants.MEDIA_PLAYBACK_STOPPED) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            holder.musicPlayStop.setImageDrawable(playDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            holder.musicEqualizer.setImageDrawable(drawable);

            holder.musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            holder.circularProgressBar.setProgressWithAnimation(0);
        } else if (mMusicFile.getIsPlaying() == AllConstants.MEDIA_PLAYBACK_FINISHED) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            holder.musicPlayStop.setImageDrawable(playDrawable);
//
            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            holder.musicEqualizer.setImageDrawable(drawable);

            holder.musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            holder.circularProgressBar.setProgressWithAnimation(0);
        } else if (mMusicFile.getIsPlaying() == AllConstants.MEDIA_PLAYBACK_PAID) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            holder.musicPlayStop.setImageDrawable(playDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            holder.musicEqualizer.setImageDrawable(drawable);

            holder.musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            holder.circularProgressBar.setProgressWithAnimation(0);

            //stop music service
            if (AppUtils.isServiceRunning(mActivity.getApplicationContext(), MediaService.class)) {
                Intent intentMediaService = new Intent(mActivity.getApplicationContext(), MediaService.class);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                mActivity.getApplicationContext().stopService(intentMediaService);
            }

            Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_buy_song_for_listening_full_song), Toast.LENGTH_LONG).show();
        }

        if (user.getId().equalsIgnoreCase(mMusicFile.getUser_id())) {
            holder.musicFreePaid.setVisibility(View.GONE);

            //Set music as free
            mMusicFile.setIs_paid("0");
        } else {
            if (mMusicFile.getIs_paid().equalsIgnoreCase("1")) {
                holder.musicFreePaid.setVisibility(View.VISIBLE);
                holder.musicFreePaid.setText(AppUtils.getUnderlinedText("$" + mMusicFile.getPrice()));
            } else {
                holder.musicFreePaid.setVisibility(View.GONE);
            }
        }

        holder.musicFreePaid.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (AppUtils.isServiceRunning(mActivity.getApplicationContext(), MediaService.class)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_stop_music_before_buying), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NetworkManager.isConnected(mActivity)) {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMusicFile.getIs_paid().equalsIgnoreCase("1")) {
                    Intent intentPaypal = new Intent(mActivity, PayPalActivity.class);
                    intentPaypal.putExtra(AllConstants.INTENT_KEY_PAYPAL_MUSIC_ITEM, mMusicFile);
                    mActivity.startActivityForResult(intentPaypal, AllConstants.REQUEST_CODE_PAYPAL);
                }
            }
        });

        holder.musicPlayStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentMediaService = null;
                if (AppUtils.isServiceRunning(mActivity.getApplicationContext(), MediaService.class)) {
                    intentMediaService = new Intent(mActivity.getApplicationContext(), MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    mActivity.getApplicationContext().stopService(intentMediaService);
                } else {
                    intentMediaService = new Intent(mActivity.getApplicationContext(), MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, mMusicFile);
                    mActivity.getApplicationContext().startService(intentMediaService);
                }
            }
        });

        holder.musicDescription.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (AppUtils.isServiceRunning(mActivity.getApplicationContext(), MediaService.class)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_stop_music_before_checking_list), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NetworkManager.isConnected(mActivity)) {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "music: adapter: " + mMusicFile.toString());

                Intent intentOwnMusicList = new Intent(mActivity, OwnMusicListActivity.class);
                intentOwnMusicList.putExtra(AllConstants.INTENT_KEY_OWN_MUSIC_LIST_ITEM_MUSIC, mMusicFile);
                intentOwnMusicList.putExtra(AllConstants.INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU, false);
                mActivity.startActivity(intentOwnMusicList);
            }
        });

        return row;
    }

    public void updateMusic(Music music) {
        int position = getItemPosition(music);
        mData.remove(position);
        mData.add(position, music);
        notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case AllConstants.REQUEST_CODE_PAYPAL: {
                if (resultCode == RESULT_OK) {
                    Music music = data.getParcelableExtra(AllConstants.INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM);
                    Log.d(TAG, "updated music: " + music.toString());

                    updateMusic(music);
                }
                break;
            }
        }
    }
}