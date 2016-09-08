package com.example.spj.mobileplayer.domain;

import java.io.Serializable;

/**
 * Created by spj on 2016/9/6.
 */
public class MediaItem implements Serializable{
    private String name;
    private String data;
    private long size;
    private long duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String artist;

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", artist='" + artist + '\'' +
                '}';
    }
}
