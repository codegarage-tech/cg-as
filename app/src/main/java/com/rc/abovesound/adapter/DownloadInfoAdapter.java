package com.rc.abovesound.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.rc.abovesound.R;
import com.rc.abovesound.model.DownloadInfo;
import com.rc.abovesound.service.DownloadService;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;
import com.reversecoder.library.event.OnSingleClickListener;

import java.util.ArrayList;
import java.util.List;

public class DownloadInfoAdapter extends BaseAdapter {
    // Simple class to make it so that we don't have to call findViewById frequently
    private static class ViewHolder {
//        TextView textView;
//        ProgressBar progressBar;
//        Button button;

        TextView musicName;
        TextView musicDescription;
        TextView musicSpentTime;
        TextView musicFreePaid;
        ImageView musicPlayStop;
        ImageView musicEqualizer;
        CircularProgressBar progressBar;

        DownloadInfo info;
    }

    private Context mContext;
    private List<DownloadInfo> mData = new ArrayList<DownloadInfo>();
    private static final String TAG = DownloadInfoAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;

    public DownloadInfoAdapter(Context context, List<DownloadInfo> objects) {
        mContext = context;
        mData = objects;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public DownloadInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemPosition(DownloadInfo music) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getPrimaryKey() == music.getPrimaryKey()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final DownloadInfo info = getItem(position);
        // We need to set the convertView's progressBar to null.

        ViewHolder holder = null;

        if (null == row) {
            row = inflater.inflate(R.layout.list_row_music, parent, false);

            holder = new ViewHolder();

//            holder.textView = (TextView) row.findViewById(R.id.downloadFileName);
//            holder.progressBar = (ProgressBar) row.findViewById(R.id.downloadProgressBar);
//            holder.button = (Button) row.findViewById(R.id.downloadButton);

            holder.musicName = (TextView) row.findViewById(R.id.tv_music_name);
            holder.musicDescription = (TextView) row.findViewById(R.id.tv_music_description);
            holder.musicSpentTime = (TextView) row.findViewById(R.id.tv_spent_time);
            holder.musicFreePaid = (TextView) row.findViewById(R.id.tv_music_free_paid);
            holder.musicPlayStop = (ImageView) row.findViewById(R.id.iv_music_play_stop);
            holder.musicEqualizer = (ImageView) row.findViewById(R.id.iv_equalizer);
            holder.progressBar = (CircularProgressBar) row.findViewById(R.id.cp_streaming_music);

            holder.info = info;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();

            holder.info.setProgressBar(null);
            holder.info = info;
            holder.info.setProgressBar(holder.progressBar);
        }

        holder.musicName.setText(info.getFileName());
        holder.progressBar.setProgress(info.getProgress());
//        holder.progressBar.setMax(info.getFileSize());
        info.setProgressBar(holder.progressBar);
//
//        holder.button.setEnabled(info.getDownloadState() == DownloadInfo.DownloadState.NOT_STARTED);
//        final Button button = holder.button;
//        holder.button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                info.setDownloadState(DownloadInfo.DownloadState.QUEUED);
//                button.setEnabled(false);
//                button.invalidate();
//                FileDownloadTask task = new FileDownloadTask(info);
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//        });

        holder.musicPlayStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentMediaService = null;
                if (AppUtils.isServiceRunning(mContext.getApplicationContext(), DownloadService.class)) {
                    intentMediaService = new Intent(mContext.getApplicationContext(), DownloadService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    mContext.getApplicationContext().stopService(intentMediaService);
                } else {
                    intentMediaService = new Intent(mContext.getApplicationContext(), DownloadService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, info);
                    mContext.getApplicationContext().startService(intentMediaService);
                }
            }
        });


        //TODO: When reusing a view, invalidate the current progressBar.

        return row;
    }

    public void updateDownloadInfo(DownloadInfo downloadInfo){
//        mInfo.setProgress(values[0]);
//        getItem(getItemPosition(downloadInfo)).getProgressBar().setProgress(downloadInfo.getProgress());
        Log.d("UpdateTest: ", downloadInfo.toString());
        CircularProgressBar bar = getItem(getItemPosition(downloadInfo)).getProgressBar();
        if (bar != null) {
            bar.setProgress(downloadInfo.getProgress());
            bar.invalidate();
        }
//        notifyDataSetChanged();
    }
}
