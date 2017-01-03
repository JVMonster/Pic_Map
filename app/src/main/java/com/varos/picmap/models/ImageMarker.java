package com.varos.picmap.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by David on 27.11.2016.
 */

public class ImageMarker  implements ClusterItem {
    public  String mImageName;
    public   String mImageUrl ;
    public  LatLng mLatLang;
   public  ImageMarker()
    {
    }

    public ImageMarker(LatLng latLng, String url, String name)
    {
        this.mLatLang=latLng;
        this.mImageName=name;
        this.mImageUrl=url;
    }
    @Override
    public LatLng getPosition() {
        return mLatLang;
    }

    public String getmMarkerName() {
        return mImageName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}
