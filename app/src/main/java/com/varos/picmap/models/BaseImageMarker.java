package com.varos.picmap.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 01.12.2016.
 */

public class BaseImageMarker extends RealmObject {
    @PrimaryKey
     public int id;
     public String url;
     public String name;
     public float lat;
     public float lon;
    public BaseImageMarker(int id, String url, String name, float lat, float lon)
    {
        this.id=id;
        this.url=url;
        this.name=name;
        this.lat=lat;
        this.lon=lon;
    }
    public BaseImageMarker()
    {}



    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
