package com.example.spj.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.spj.mobileplayer.IMusicPlayerService;
import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.AudioPlayerActivity;
import com.example.spj.mobileplayer.domain.MediaItem;
import com.example.spj.mobileplayer.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by spj on 2016/9/11.
 */
public class MusicPlayerService extends Service {

    public static final String OPEN_COMPLETE = "com.mobileplayer.OPEN_COMPLETE";

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {

            service.pause();
        }

        @Override
        public void next() throws RemoteException {

            service.next();
        }

        @Override
        public void pre() throws RemoteException {

            service.pre();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {

            service.setPlayMode(playmode);
        }

        @Override
        public int getPlaymode() throws RemoteException {

            return service.getPlayMode();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void seekTo(int progress) throws RemoteException {

            service.seekTo(progress);
        }

        @Override
        public void openAudio(int position) throws RemoteException {

            service.openAudio(position);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {

            service.notifyChange(action);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return mediaItem.getData();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };


    /**
     * 顺序播放
     */
    public static final int REPEAT_NOMAL = 1;

    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;


    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;
    /**
     * 音频列表
     */
    private ArrayList<MediaItem> mediaItems;

    /**
     * 播放音频，播放视频
     */
    private MediaPlayer mediaPlayer;

    private int playmode = REPEAT_NOMAL;
    private NotificationManager manager;
    //音频在列表中的位置
    private int position;
    private MediaItem mediaItem;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //加载音频 列表
        playmode = CacheUtils.getPlaymode(this, "playmode");
        getData();
    }

    private void getData() {
        //主线程
        new Thread() {
            @Override
            public void run() {
                super.run();

                //子线程

                mediaItems = new ArrayList<MediaItem>();

                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//视频的uri
                String[] objs = new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件的名称
                        MediaStore.Audio.Media.SIZE,//文件大小
                        MediaStore.Audio.Media.DURATION,//视频文件的时长
                        MediaStore.Audio.Media.DATA,//视频文件绝对地址
                        MediaStore.Audio.Media.ARTIST//艺术家


                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long size = cursor.getLong(1);
                        mediaItem.setSize(size);
                        long duration = cursor.getLong(2);
                        mediaItem.setDuration(duration);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        //添加到集合中
                        mediaItems.add(mediaItem);
                    }

                    cursor.close();
                }

            }
        }.start();
    }

    private void start() {

        mediaPlayer.start();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification", true);//表示从状态栏进入的

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("影音播放")
                .setContentText("正在播放" + getName())
                .setContentIntent(pendingIntent)
                .build();
        //设置属性
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(1, notification);

    }

    //上一首
    private void pre() {
        //1.根据不同的播放模式，设置下一个播放的位置
        setPrePosition();
        //2.根据位置打开不同的音频
        openPosition();
    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            position--;
            if (position < 0) {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE || playmode == MusicPlayerService.REPEAT_ALL) {

            position--;
            if (position < 0) {
                position = mediaItems.size() - 1;
            }
        }
    }

    //点击按钮后的下一首
    private void next() {

        //1.根据不同的播放模式，设置下一个播放的位置
        setNextPosition();
        //2.根据位置打开不同的音频
        openPosition();
    }

    private void setNextPosition() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE || playmode == MusicPlayerService.REPEAT_ALL) {

            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        }
    }

    //自动的播放下一首
    private void AutoNext() {
        //1.根据不同的播放模式，设置下一个播放的位置
        setNextAutoPosition();
        //2.根据位置打开不同的音频
        openPosition();
    }

    private void openPosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            if(position<0) {
                openAudio(0);
            }else if (position <= mediaItems.size() - 1) {
                openAudio(position);
            } else {
                //越界的
                openAudio(mediaItems.size() - 1);
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE || playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        }
    }

    private void setNextAutoPosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        }
    }

    //暂停
    private void pause() {
        mediaPlayer.pause();
        manager.cancel(1);

    }

    //设置播放模式
    private void setPlayMode(int playMode) {

        this.playmode = playMode;
        CacheUtils.putPlaymode(this, "playmode", playmode);
    }

    //得到播放模式
    private int getPlayMode() {
        return playmode;
    }

    //得到艺术家
    private String getArtist() {
        if (mediaItem != null) {
            return mediaItem.getArtist();
        }
        return "";
    }

    //得到歌曲名称
    private String getName() {
        if (mediaItem != null) {
            return mediaItem.getName();
        }
        return "";
    }

    //得到当前进度
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    //得到总时长
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    //音频的拖动
    private void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }


    //根据位置播放对应音频文件
    private void openAudio(int position) {
        if (mediaItems != null && mediaItems.size() > 0) {
            this.position = position;
            mediaItem = mediaItems.get(position);
            //避免同时播放几首音乐
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer = null;
            }

            try {
                //重新创建mediaplayer
                mediaPlayer = new MediaPlayer();
//            if(playmode == MusicPlayerService.REPEAT_SINGLE) {
//                mediaPlayer.setLooping(true);
//            }else {
//                mediaPlayer.setLooping(false);
//            }
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                //设置播放地址
                mediaPlayer.setDataSource(mediaItem.getData());
                //开始异步准备播放
//                mediaPlayer.prepare();//播放本地音视频
//                mediaPlayer.start();
                //可以播放本地和网络的
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(MusicPlayerService.this, "数据没有准备好", Toast.LENGTH_SHORT).show();
        }

    }

    //发广播
    private void notifyChange(String action) {

//        Intent intent = new Intent(action);
//        sendBroadcast(intent);
        //3.enventBus 事件发布
        EventBus.getDefault().post(mediaItem);
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            start();
            //发广播
            notifyChange(OPEN_COMPLETE);
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            next();
            return true;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            AutoNext();
        }
    }
}
