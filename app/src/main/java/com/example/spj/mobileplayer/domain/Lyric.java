package com.example.spj.mobileplayer.domain;

/**
 * Created by spj on 2016/9/12.
 */
public class Lyric {

    //歌词内容
    private String content;
    //时间戳
    private long timePoint;
    //高亮变色
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
