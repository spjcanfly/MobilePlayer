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
    private String imageUrl;
    private String desc;
    private String artist;
    //专辑的图片的id
    private long album_id;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbum_id() {
        return album_id;

    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", imageUrl='" + imageUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", artist='" + artist + '\'' +
                ", album_id=" + album_id +
                ", id=" + id +
                '}';
    }
}
