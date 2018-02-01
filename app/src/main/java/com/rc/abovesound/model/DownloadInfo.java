package com.rc.abovesound.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.reversecoder.library.random.RandomManager;

public class DownloadInfo implements Parcelable {

  private long primaryKey = -1;
  private final static String TAG = DownloadInfo.class.getSimpleName();
  private  String fileName;
  private  String filePath;
  private volatile int progress = 0;
  private volatile CircularProgressBar progressBar = null;
  private volatile TextView spentTime = null;
  private volatile ImageView playPauseButton = null;

  public DownloadInfo(){
    this.primaryKey = RandomManager.getRandom(10000000);
//    this.progress = 0;
    this.progressBar = null;
    this.spentTime = null;
    this.playPauseButton = null;
  }

  public DownloadInfo(String fileName, String filePath) {
    this.primaryKey = RandomManager.getRandom(10000000);
//    this.progress = 0;
    this.progressBar = null;
    this.spentTime = null;
    this.playPauseButton = null;

    this.fileName = fileName;
    this.filePath = filePath;
  }

  public long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public CircularProgressBar getProgressBar() {
    return progressBar;
  }

  public void setProgressBar(CircularProgressBar progressBar) {
    this.progressBar = progressBar;
  }

  public TextView getSpentTime() {
    return spentTime;
  }

  public void setSpentTime(TextView spentTime) {
    this.spentTime = spentTime;
  }

  public ImageView getPlayPauseButton() {
    return playPauseButton;
  }

  public void setPlayPauseButton(ImageView playPauseButton) {
    this.playPauseButton = playPauseButton;
  }

  @Override
  public String toString() {
    return "DownloadInfo{" +
            "primaryKey=" + primaryKey +
            ", fileName='" + fileName + '\'' +
            ", filePath='" + filePath + '\'' +
            ", progress=" + progress +
            '}';
  }

  //parcelable methods
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(primaryKey);
    dest.writeString(fileName);
    dest.writeString(filePath);
    dest.writeInt(progress);
  }

  // Creator
  public static final Parcelable.Creator CREATOR
          = new Parcelable.Creator() {
    public DownloadInfo createFromParcel(Parcel in) {
      return new DownloadInfo(in);
    }

    public DownloadInfo[] newArray(int size) {
      return new DownloadInfo[size];
    }
  };

  // "De-parcel object
  public DownloadInfo(Parcel in) {
    primaryKey = in.readLong();
    fileName = in.readString();
    filePath = in.readString();
    progress = in.readInt();
  }
}
