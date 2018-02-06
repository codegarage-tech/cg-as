package com.rc.abovesound.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
public class OwnMusicListViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Music> mData;
    private static LayoutInflater inflater = null;
    private String TAG = OwnMusicListViewAdapter.class.getSimpleName();
    private UserData user;
    private Music lastSelectedMusic = null;

    private static class ViewHolder {

        TextView musicName;
        TextView musicDescription;
        TextView musicSpentTime;
        TextView musicFreePaid;
        ImageView musicPlayStop;
        ImageView musicEqualizer;
        CircularProgressBar progressBar;

        Music music;
    }

    public OwnMusicListViewAdapter(Activity activity) {
        mActivity = activity;
        mData = new ArrayList<Music>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    public int getItemPositionByFilePath(Music music) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getFile_path().equalsIgnoreCase(music.getFile_path())) {
                return i;
            }
        }
        return -1;
    }

    public int getItemPositionByPrimaryKey(Music music) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getPrimaryKey() == music.getPrimaryKey()) {
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View vi = convertView;
        final Music mMusic = getItem(position);
        ViewHolder holder = null;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_row_music, parent, false);
            holder = new ViewHolder();

            holder.musicName = (TextView) vi.findViewById(R.id.tv_music_name);
            holder.musicDescription = (TextView) vi.findViewById(R.id.tv_music_description);
            holder.musicSpentTime = (TextView) vi.findViewById(R.id.tv_spent_time);
            holder.musicFreePaid = (TextView) vi.findViewById(R.id.tv_music_free_paid);
            holder.musicPlayStop = (ImageView) vi.findViewById(R.id.iv_music_play_stop);
            holder.musicEqualizer = (ImageView) vi.findViewById(R.id.iv_equalizer);
            holder.progressBar = (CircularProgressBar) vi.findViewById(R.id.cp_streaming_music);
            holder.music = mMusic;

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();

            holder.music.setProgressBar(null);
            holder.music.setEqualizer(null);
            holder.music.setPlayPauseButton(null);
            holder.music.setSpentTime(null);

            holder.music = mMusic;

            holder.music.setProgressBar(holder.progressBar);
            holder.music.setEqualizer(holder.musicEqualizer);
            holder.music.setPlayPauseButton(holder.musicPlayStop);
            holder.music.setSpentTime(holder.musicSpentTime);
        }

        mMusic.setProgressBar(holder.progressBar);
        mMusic.setSpentTime(holder.musicSpentTime);
        mMusic.setEqualizer(holder.musicEqualizer);
        mMusic.setPlayPauseButton(holder.musicPlayStop);

        holder.progressBar.setProgress(mMusic.getProgress());
        holder.musicSpentTime.setText(mMusic.getSpentTimeText());
        holder.musicPlayStop.setImageDrawable(mMusic.getBgPlayPauseButton());
        holder.musicEqualizer.setImageDrawable((mMusic.getBgEqualizer() == 1) ? AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.PLAYING) : AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE));
        holder.musicName.setText(mMusic.getMusic_title());
        holder.musicDescription.setText(AppUtils.getUnderlinedText(mMusic.getFirst_name() + " " + mMusic.getLast_name()));

        if (((OwnMusicListActivity) mActivity).isFromMenu) {
            holder.musicFreePaid.setVisibility(View.GONE);

            //Set music as free
            mMusic.setIs_paid("0");
        } else {
            if (((OwnMusicListActivity) mActivity).mMusic.getUser_id().equalsIgnoreCase(user.getId())) {
                holder.musicFreePaid.setVisibility(View.GONE);

                //Set music as free
                mMusic.setIs_paid("0");
            } else {
                if (mMusic.getIs_paid().equalsIgnoreCase("1")) {
                    holder.musicFreePaid.setVisibility(View.VISIBLE);
                    holder.musicFreePaid.setText(AppUtils.getUnderlinedText("$" + mMusic.getPrice()));
                } else {
                    holder.musicFreePaid.setVisibility(View.GONE);
                }
            }
        }

        holder.musicFreePaid.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                if (!NetworkManager.isConnected(mActivity)) {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMusic.getIs_paid().equalsIgnoreCase("1")) {
                    Intent intentPaypal = new Intent(mActivity, PayPalActivity.class);
                    intentPaypal.putExtra(AllConstants.INTENT_KEY_PAYPAL_MUSIC_ITEM, mMusic);
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

                    // For stopping running music
                    if (lastSelectedMusic != null) {
                        if (lastSelectedMusic.getFile_path().equalsIgnoreCase(mMusic.getFile_path())) {
                            return;
                        }
                    }
                }

                intentMediaService = new Intent(mActivity.getApplicationContext(), MediaService.class);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, mMusic);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_TYPE, MediaService.TYPE.OWN_MUSIC.name());
                mActivity.getApplicationContext().startService(intentMediaService);

                lastSelectedMusic = mMusic;
            }
        });

        return vi;
    }

    public void updateMusic(Music music) {
        Log.d("UpdateTest: ", music.toString());

        int position = getItemPositionByFilePath(music);
        if (position != -1) {
            Music listItem = getItem(position);
            if (listItem != null) {
                CircularProgressBar progressBar = listItem.getProgressBar();
                ImageView musicPlayStop = listItem.getPlayPauseButton();
                ImageView musicEqualizer = listItem.getEqualizer();
                TextView musicSpentTime = listItem.getSpentTime();

                if (progressBar != null) {
                    if (music.getProgress() == 0) {
                        progressBar.setProgressWithAnimation(music.getProgress());
                    } else {
                        progressBar.setProgress(music.getProgress());
                    }
                    progressBar.invalidate();
                }
                if (musicPlayStop != null) {
                    musicPlayStop.setImageDrawable(music.getBgPlayPauseButton());
                    musicPlayStop.invalidate();
                }
                if (musicEqualizer != null) {
                    musicEqualizer.setImageDrawable((music.getBgEqualizer() == 1) ? AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.PLAYING) : AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE));
                    musicEqualizer.invalidate();
                }
                if (musicSpentTime != null) {
                    musicSpentTime.setText(music.getSpentTimeText());
                    musicSpentTime.invalidate();
                }

                if (music.getIsPlaying() == AllConstants.MEDIA_PLAYBACK_PAID) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_buy_song_for_listening_full_song), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void updateBoughtItem(Music music) {
        int position = getItemPositionByPrimaryKey(music);
        if (position != -1) {
            mData.remove(position);
            mData.add(position, music);
            notifyDataSetChanged();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case AllConstants.REQUEST_CODE_PAYPAL: {
                if (resultCode == RESULT_OK) {
                    Music music = data.getParcelableExtra(AllConstants.INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM);
                    Log.d(TAG, "updated music: " + music.toString());

                    if (music != null) {
                        updateBoughtItem(music);
                    }
                }
                break;
            }
        }
    }
}