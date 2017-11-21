package com.reversecoder.mh.adapter;

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
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.mh.R;
import com.reversecoder.mh.activity.OwnMusicListActivity;
import com.reversecoder.mh.model.Music;
import com.reversecoder.mh.model.UserData;
import com.reversecoder.mh.paypal.PayPalActivity;
import com.reversecoder.mh.service.MediaService;
import com.reversecoder.mh.util.AllConstants;
import com.reversecoder.mh.util.AppUtils;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.reversecoder.mh.util.AllConstants.INTENT_KEY_PAYPAL_MUSIC_ITEM;
import static com.reversecoder.mh.util.AllConstants.INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM;
import static com.reversecoder.mh.util.AllConstants.MEDIA_PLAYBACK_FINISHED;
import static com.reversecoder.mh.util.AllConstants.MEDIA_PLAYBACK_PAID;
import static com.reversecoder.mh.util.AllConstants.MEDIA_PLAYBACK_STOPPED;
import static com.reversecoder.mh.util.AllConstants.MEDIA_PLAYER_RUNNING;
import static com.reversecoder.mh.util.AllConstants.REQUEST_CODE_PAYPAL;
import static com.reversecoder.mh.util.AllConstants.SESSION_USER_DATA;
import static com.reversecoder.mh.util.AppUtils.getUnderlinedText;
import static com.reversecoder.mh.util.AppUtils.isServiceRunning;

/**
 * @author Md. Rashadul Alam
 */
public class OwnMusicListViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Music> mData;
    private static LayoutInflater inflater = null;
    private String TAG = OwnMusicListViewAdapter.class.getSimpleName();
    private UserData user;

    public OwnMusicListViewAdapter(Activity activity) {
        mActivity = activity;
        mData = new ArrayList<Music>();
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(mActivity, SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(mActivity, SESSION_USER_DATA));
            user = UserData.getResponseObject(SessionManager.getStringSetting(mActivity, SESSION_USER_DATA), UserData.class);
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
            if ((mData.get(i)).getMusic_title().contains(music.getMusic_title())) {
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
        musicDescription.setText(mMusicFile.getDescription());

        if (mMusicFile.getIsPlaying() == MEDIA_PLAYER_RUNNING) {
            Drawable stopDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_stop);
            musicPlayStop.setImageDrawable(stopDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.PLAYING);
            musicEqualizer.setImageDrawable(drawable);

            //to avoid arethmetic exception
            if (mMusicFile.getTotalTime() > 0) {
                Log.d(TAG, "total: " + mMusicFile.getTotalTime() + "");
                Log.d(TAG, "last progress: " + mMusicFile.getLastPlayed() + "");
                int progress = (int) (((float) mMusicFile.getLastPlayed() / mMusicFile.getTotalTime()) * 100);
                Log.d(TAG, "progress: " + progress + "");
                circularProgressBar.setProgress(progress);
            }
            musicSpentTime.setText(AppUtils.milliSecondsToTimer(mMusicFile.getLastPlayed()) + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
        } else if (mMusicFile.getIsPlaying() == MEDIA_PLAYBACK_STOPPED) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            musicPlayStop.setImageDrawable(playDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            musicEqualizer.setImageDrawable(drawable);

            musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            circularProgressBar.setProgressWithAnimation(0);
        } else if (mMusicFile.getIsPlaying() == MEDIA_PLAYBACK_FINISHED) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            musicPlayStop.setImageDrawable(playDrawable);
//
            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            musicEqualizer.setImageDrawable(drawable);

            musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            circularProgressBar.setProgressWithAnimation(0);
        } else if (mMusicFile.getIsPlaying() == MEDIA_PLAYBACK_PAID) {
            Drawable playDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_play);
            musicPlayStop.setImageDrawable(playDrawable);

            Drawable drawable = AppUtils.getDrawableByState(mActivity, AppUtils.MEDIA_STATE.NONE);
            musicEqualizer.setImageDrawable(drawable);

            musicSpentTime.setText("0.00" + "/" + AppUtils.milliSecondsToTimer(mMusicFile.getTotalTime()));
            circularProgressBar.setProgressWithAnimation(0);

            //stop music service
            if (isServiceRunning(mActivity, MediaService.class)) {
                Intent intentMediaService = new Intent(mActivity, MediaService.class);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                mActivity.stopService(intentMediaService);
            }

            Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_buy_song_for_listening_full_song), Toast.LENGTH_LONG).show();
        }

        if (((OwnMusicListActivity) mActivity).isFromMenu) {
            musicFreePaid.setVisibility(View.GONE);
        } else {
            if (((OwnMusicListActivity) mActivity).mMusic.getUser_id().equalsIgnoreCase(user.getId())) {
                musicFreePaid.setVisibility(View.GONE);
            } else {
                if (mMusicFile.getIs_paid().equalsIgnoreCase("1")) {
                    musicFreePaid.setVisibility(View.VISIBLE);
                    musicFreePaid.setText(getUnderlinedText("$" + mMusicFile.getPrice()));
                } else {
                    musicFreePaid.setVisibility(View.GONE);
                }
            }
        }

        musicFreePaid.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(mActivity, MediaService.class)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_stop_music_before_buying), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NetworkManager.isConnected(mActivity)) {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMusicFile.getIs_paid().equalsIgnoreCase("1")) {
                    Intent intentPaypal = new Intent(mActivity, PayPalActivity.class);
                    intentPaypal.putExtra(INTENT_KEY_PAYPAL_MUSIC_ITEM, mMusicFile);
                    mActivity.startActivityForResult(intentPaypal, REQUEST_CODE_PAYPAL);
                }
            }
        });

        musicPlayStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentMediaService = null;
                if (isServiceRunning(mActivity, MediaService.class)) {
                    intentMediaService = new Intent(mActivity, MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    mActivity.stopService(intentMediaService);
                } else {
                    intentMediaService = new Intent(mActivity, MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, mMusicFile);
                    mActivity.startService(intentMediaService);
                }
            }
        });

        musicDescription.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
//                if (isServiceRunning(mActivity, MediaService.class)) {
//                    Toast.makeText(mActivity, mActivity.getString(R.string.toast_please_stop_music_before_checking_list), Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (!NetworkManager.isConnected(mActivity)) {
//                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Log.d(TAG, "music: adapter: " + mMusicFile.toString());
//
//                Intent intentOwnMusicList = new Intent(mActivity, OwnMusicListActivity.class);
//                intentOwnMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_ITEM_MUSIC, mMusicFile);
//                intentOwnMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU, false);
//                mActivity.startActivity(intentOwnMusicList);
            }
        });

        return vi;
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
            case REQUEST_CODE_PAYPAL: {
                if (resultCode == RESULT_OK) {
                    Music music = data.getParcelableExtra(INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM);
                    Log.d(TAG, "updated music: " + music.toString());

                    updateMusic(music);
                }
                break;
            }
        }
    }
}