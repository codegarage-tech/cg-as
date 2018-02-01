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
import com.rc.abovesound.model.DownloadInfo;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;

public class DownloadService extends Service {

    DownloadInfo downloadInfo = null;
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
                        downloadInfo = intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC);
                        Log.d("From service: ", downloadInfo.toString());

                        audioPlayer = new AudioPlayer(getApplicationContext());
                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        audioPlayer.setDataSource(Uri.parse(downloadInfo.getFilePath()));

                        //Set downloadInfo listener
                        audioPlayer.setOnCompletionListener(new OnCompletionListener() {
                            @Override
                            public void onCompletion() {
                                if (audioPlayer != null) {
                                    audioPlayer.pause();

//                                    downloadInfo.setIsPlaying(AllConstants.MEDIA_PLAYBACK_FINISHED);
                                    sendUpdateToActivity(downloadInfo);
                                }
                            }
                        });

                        //Start downloadInfo
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
//            downloadInfo.setIsPlaying(AllConstants.MEDIA_PLAYBACK_STOPPED);
            sendUpdateToActivity(downloadInfo);

            audioPlayer.stopPlayback();
            audioPlayer.release();
            audioPlayer = null;
        }

        handler.removeCallbacks(runnableUpdate);
        handler = null;
    }

    private void sendUpdateToActivity(DownloadInfo music) {
        broadcastIntentActivityUpdate.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE, music);
        sendBroadcast(broadcastIntentActivityUpdate);
    }

    private void updateSeekProgress() {
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
//                if (downloadInfo.getIs_paid().equalsIgnoreCase("1") && AppUtils.isPlayedFor(audioPlayer.getCurrentPosition(), AllConstants.DEFAULT_PAID_PLAYBACK)) {
//                    audioPlayer.pause();
//
//                    downloadInfo.setIsPlaying(AllConstants.MEDIA_PLAYBACK_PAID);
//                    sendUpdateToActivity(downloadInfo);
//                } else {
//                    downloadInfo.setIsPlaying(AllConstants.MEDIA_PLAYER_RUNNING);
//                    downloadInfo.setTotalTime((int) audioPlayer.getDuration());
//                    downloadInfo.setLastPlayed((int) audioPlayer.getCurrentPosition());
                    if ((int) audioPlayer.getDuration() > 0) {
                        Log.d("UpdateTest: ","getCurrentPosition: "+audioPlayer.getCurrentPosition()+"");
                        Log.d("UpdateTest: ","getDuration: "+audioPlayer.getDuration()+"");
                        downloadInfo.setProgress((int) (((float) ((int)audioPlayer.getCurrentPosition()) / ((int)audioPlayer.getDuration())) * 100));
                        Log.d("UpdateTest: ",downloadInfo.getProgress()+"");
                    }

                    sendUpdateToActivity(downloadInfo);

                    handler.postDelayed(runnableUpdate, 1000);
//                }
            } else {
//                downloadInfo.setIsPlaying(AllConstants.MEDIA_PLAYBACK_STOPPED);
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
