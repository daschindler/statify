package com.schindlerdavid.statify.entity;

import android.graphics.Bitmap;

public class Track {
    private String name;
    //private Bitmap cover;
    private String cover_url;

    public String getName() {
        return name;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Track(String name, String cover_url) {
        this.cover_url = cover_url;

        this.name = name;
    }
}
