package com.hackathon.myapplication.elearn;

import java.io.Serializable;

public class E_LearnModel implements Serializable {
    private String uid;
    private String url;
    private String title;
    private String pathspec;
    private String userName;

    public String getPathspec() {
        return pathspec;
    }

    public void setPathspec(String pathspec) {
        this.pathspec = pathspec;
    }

    private String description;
    private long date;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
