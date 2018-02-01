package com.rc.abovesound.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;

public class MediaService extends Service {

    Music music = null;
    AudioPlayer audioPlayer = null;
    Intent broadcastIntentActivityUpdate;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastIntentActivityUpdate = new Intent(AllConstants.INTENT_FILTER_ACTIVITY_UPDATE);
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

                        audioPlayer = new AudioPlayer(getApplicationContext());
                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        audioPlayer.setDataSource(Uri.parse(music.getFile_path()));

                        //Set music listener
                        audioPlayer.setOnCompletionListener(new OnCompletionListener() {
                            @Override
                            public void onCompletion() {
                                if (audioPlayer != null) {
                                    audioPlayer.pause();

                                    music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_FINISHED);
                                    sendUpdateToActivity(music);
                                }
                            }
                        });

                        //Start music
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
                    sendUpdateToActivity(music);
                } else {
                    music.setIsPlaying(AllConstants.MEDIA_PLAYER_RUNNING);
                    music.setTotalTime((int) audioPlayer.getDuration());
                    music.setLastPlayed((int) audioPlayer.getCurrentPosition());
                    if ((int) audioPlayer.getDuration() > 0) {
                        music.setProgress((int) (((int) audioPlayer.getCurrentPosition() / (int) audioPlayer.getDuration()) * 100));
                    }

                    sendUpdateToActivity(music);

                    handler.postDelayed(runnableUpdate, 1000);
                }
            } else {
                music.setIsPlaying(AllConstants.MEDIA_PLAYBACK_STOPPED);
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
