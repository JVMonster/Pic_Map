package com.varos.picmap.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by David on 09.12.2016.
 */

public class ImageModel implements Parcelable {
    String name, url;
    Context mContext;

    public Context getContext() {
        return mContext;
    }

    public ImageModel() {

    }

    public ImageModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    protected ImageModel(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }
}
