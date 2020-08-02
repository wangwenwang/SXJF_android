package com.kaidongyuan.app.kdydriver.bean.baidumap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *@auther: Tom
 *离线城市地图信息类
 */
public class OfflineCitymap implements Parcelable {
    private String cityName;
    private int   cityCode;
    private int progress;
    private CitymapFlag flag= CitymapFlag.NO_STATUS;

    protected OfflineCitymap(Parcel in) {
        cityName = in.readString();
        cityCode = in.readInt();
        progress = in.readInt();
    }
    public OfflineCitymap(){
    }
    public static final Creator<OfflineCitymap> CREATOR = new Creator<OfflineCitymap>() {
        @Override
        public OfflineCitymap createFromParcel(Parcel in) {
            return new OfflineCitymap(in);
        }

        @Override
        public OfflineCitymap[] newArray(int size) {
            return new OfflineCitymap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(cityName);
            dest.writeInt(cityCode);
            dest.writeInt(progress);
            dest.writeValue(flag);
    }

    public enum CitymapFlag{
        NO_STATUS,PAUSE,DOWNLOADING,FINISH
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public CitymapFlag getFlag() {
        return flag;
    }

    public void setFlag(CitymapFlag flag) {
        this.flag = flag;
    }
}
