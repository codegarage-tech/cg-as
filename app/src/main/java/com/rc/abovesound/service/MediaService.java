package com.rc.abovesound.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.rc.abovesound.R;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;

import static com.rc.abovesound.util.AllConstants.DEFAULT_SPENT_TIME_TEXT;

public class MediaService extends Service {

    Music music = null;
    AudioPlayer audioPlayer = null;
    Intent broadcastIntentActivityUpdate;
    private Handler handler = new Handler();
    TYPE type;

    public enum TYPE {HOME_MUSIC, OWN_MUSIC, BOUGHT_MUSIC}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int action = intent.getIntExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, -1);
            switch (action) {
                case AllConstants.EXTRA_ACTION_START: {
                    if (intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC) != null) {
                        music = intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC);
                        Log.d("From service: ", music.toString());
                        type = TYPE.valueOf(intent.getStringExtra(AllConstants.KEY_INTENT_EXTRA_TYPE));

                        //Set update broad cast type
                        switch (type) {
                            case HOME_MUSIC:
                                broadcastIntentActivityUpdate = new Intent(AllConstants.INTENT_FILTER_HOME_MUSIC_UPDATE);
                                break;
                            case OWN_MUSIC:
                                broadcastIntentActivityUpdate = new Intent(AllConstants.INTENT_FILTER_OWN_MUSIC_UPDATE);
                                break;
                            case BOUGHT_MUSIC:
                                broadcastIntentActivityUpdate = new Intent(AllConstants.INTENT_FILTER_BOUGHT_MUSIC_UPDATE);
                                break;
                        }

                        //Set player
                        audioPlayer = new AudioPlayer(getApplicationContext());
                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        audioPlayer.setDataSource(Uri.parse(music.getFile_path()));

                        //Set player listener
                        audioPlayer.setOnCompletionListener(new OnCompletionListener() {
                            @Override
                            public void onCompletion() {
                                if (audioPlayer != null) {
                                    audioPlayer.pause();

                                    music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_FINISHED);

                                    music.setProgress(0);
                                    music.setBgEqualizer(0);
                                    Drawable playDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play);
                                    music.setBgPlayPauseButton(playDrawable);
                                    music.setSpentTimeText(DEFAULT_SPENT_TIME_TEXT);

                                    sendUpdateToActivity(music);
                                }
                            }
                        });

                        //Start player
                        audioPlayer.prepareAsync();
                        audioPlayer.start();

                        updateSeekProgress();
                    }
                    break;
                }
                case AllConstants.EXTRA_ACTION_STOP: {

                    destroyService();

                    break;
                }
                default: {
                    break;
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        destroyService();

        super.onDestroy();
    }

    private void destroyService() {
        if (audioPlayer.isPlaying()) {
            music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_STOPPED);

            music.setProgress(0);
            music.setBgEqualizer(0);
            Drawable playDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play);
            music.setBgPlayPauseButton(playDrawable);
            music.setSpentTimeText(DEFAULT_SPENT_TIME_TEXT);

            sendUpdateToActivity(music);

            audioPlayer.stopPlayback();
            audioPlayer.release();
            audioPlayer = null;
        }

        handler.removeCallbacks(runnableUpdate);
        handler = null;
    }

    private void sendUpdateToActivity(Music music) {
        broadcastIntentActivityUpdate.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE, music);
        sendBroadcast(broadcastIntentActivityUpdate);
    }

    private void updateSeekProgress() {
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                if (music.getIs_paid().equalsIgnoreCase("1") && AppUtils.isPlayedFor(audioPlayer.getCurrentPosition(), AllConstants.DEFAULT_PAID_PLAYBACK)) {
                    audioPlayer.pause();

                    music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_PAID);
                    music.setProgress(0);
                    music.setBgEqualizer(0);
                    Drawable playDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play);
                    music.setBgPlayPauseButton(playDrawable);
                    music.setSpentTimeText(DEFAULT_SPENT_TIME_TEXT);

                    sendUpdateToActivity(music);
                } else {
                    music.setIsPlaying(AllConstants.MEDIA_PLAYER_RUNNING);
                    music.setTotalTime((int) audioPlayer.getDuration());
                    music.setLastPlayed((int) audioPlayer.getCurrentPosition());
                    if ((int) audioPlayer.getDuration() > 0) {
                        Log.d("UpdateTest: ", "getCurrentPosition: " + audioPlayer.getCurrentPosition() + "");
                        Log.d("UpdateTest: ", "getDuration: " + audioPlayer.getDuration() + "");
                        music.setProgress((int) (((float) ((int) audioPlayer.getCurrentPosition()) / ((int) audioPlayer.getDuration())) * 100));
                        Log.d("UpdateTest: ", music.getProgress() + "");
                        music.setSpentTimeText(AppUtils.milliSecondsToTimer(music.getLastPlayed()) + "/" + AppUtils.milliSecondsToTimer(music.getTotalTime()));
                    }
                    music.setBgEqualizer(1);
                    Drawable stopDrawable = ContextCompat.getDrawable(this, R.drawable.ic_stop);
                    music.setBgPlayPauseButton(stopDrawable);

                    sendUpdateToActivity(music);

                    handler.postDelayed(runnableUpdate, 1000);
                }
            } else {
                music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_STOPPED);

                music.setProgress(0);
                music.setBgEqualizer(0);
                Drawable playDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play);
                music.setBgPlayPauseButton(playDrawable);
                music.setSpentTimeText(DEFAULT_SPENT_TIME_TEXT);
            }
        }
    }

    private final Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };
}
