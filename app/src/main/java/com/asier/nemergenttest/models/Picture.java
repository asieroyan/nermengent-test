package com.asier.nemergenttest.models;

import java.util.Date;

public class Picture {
    private final Date date;
    private final String location;
    private final String route;

    public Picture(Date date, String location, String route) {
        this.date = date;
        this.location = location;
        this.route = route;
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
}
