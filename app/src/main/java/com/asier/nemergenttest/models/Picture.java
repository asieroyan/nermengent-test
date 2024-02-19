package com.asier.nemergenttest.models;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.Date;

public class Picture {
    private final int id;
    private final Date date;
    private final String location;
    private final String route;

    public Picture(int id, Date date, String location, String route) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.route = route;
    }

    public int id () {
        return this.id;
    }

    public Date getDate () {
        return this.date;
    }

    public String getLocation () {
        return this.location;
    }

    public String getRoute () {
        return this.route;
    }

    public Drawable getDrawable () {
        return Drawable.createFromPath(this.route);
    }
}
