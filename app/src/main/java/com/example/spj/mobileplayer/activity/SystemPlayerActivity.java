package com.example.spj.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.domain.MediaItem;
import com.example.spj.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by spj on 2016/9/6.
 */
public class SystemPlayerActivity extends Activity {

    //视频进度的更新
    private static final int PROGRESS = 1;
    //是否隐藏控制面板
    private static final int HIDE_MEDIACONTROLLER = 2;
    //默认屏幕
    private static final int SCREEN_DEFAULT = 3;
    //全屏
    private static final int SCREEN_FULL = 4;
    @Bind(R.id.videoview)
    com.example.spj.mobileplayer.view.VideoView videoview;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_battery)
    ImageView ivBattery;
    @Bind(R.id.tv_sysytem_time)
    TextView tvSysytemTime;
    @Bind(R.id.btn_voice)
    Button btnVoice;
    @Bind(R.id.seekbar_voice)
    SeekBar seekbarVoice;
    @Bind(R.id.btn_switch_player)
    Button btnSwitchPlayer;
    @Bind(R.id.ll_top)
    LinearLayout llTop;
    @Bind(R.id.tv_currenttime)
    TextView tvCurrenttime;
    @Bind(R.id.seekbar_video)
    SeekBar seekbarVideo;
    @Bind(R.id.tv_duration)
    TextView tvDuration;
    @Bind(R.id.btn_exit)
    Button btnExit;
    @Bind(R.id.btn_pre)
    Button btnPre;
    @Bind(R.id.btn_play_pause)
    Button btnPlayPause;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.btn_switch_screen)
    Button btnSwitchScreen;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    private Uri uri;
    private int position;
    private ArrayList<MediaItem> mediaItems;
    private Utils utils;
    private boolean isShowMediaController = false;
    private GestureDetector gd;
    private boolean isFullScreen = false;
    private int screenWidth;
    private int screenHeight;
    private int videoWidth;
    private int videoHeight;
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;
    private boolean isMute=false;
    private MyBroadcastReceiver receiver;
    private int mVol;
    private float startY;
    private int touchRang;
    private float startX;
    private int touchWidth;
    private int duration;
    private int mProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_player);

        //初始化数据
        initData();

        //ButterKnife插件找控件
        ButterKnife.bind(this);

        //最大声音和seekbar关联
        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);

        //得到传递过来的视频的位置数据
        getData();

        //设置播放视频的监听
        setListener();

        //设置播放
        setData();


//        //设置系统的控制面板
//        videoview.setMediaController(new MediaController(this));
    }

    private void initData() {
        utils = new Utils();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //0~15
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //得到屏幕的宽和高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        Log.e("TAG", "screenWidth"+screenWidth);
        Log.e("TAG", "screenHeight"+screenHeight);

        //1.创建手势识别器
        gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (isFullScreen) {
                    //之前为全屏，点击后设置为默认
                    setVideoType(SCREEN_DEFAULT);
                } else {
                    //之前为默认，点击后设置为全屏
                    setVideoType(SCREEN_FULL);
                }

                return super.onDoubleTap(e);
            }

            //单击屏幕
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    //隐藏屏幕
                    isShowMediaController = false;
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    //显示屏幕
                    isShowMediaController = true;
                    hideMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 3000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                startAndPause();
                super.onLongPress(e);
            }
        });

        //2.注册监听电量变化的广播
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //0~100电量值
            int level = intent.getIntExtra("level", 0);
            //主线程
            setBattery(level);
        }
    }

    private void setBattery(int level) {

        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }
    private int downX,downY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传递给手势识别器
        gd.onTouchEvent(event);
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                //1.按下,记录按下这个时刻的当前音量
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mProgress = seekbarVideo.getProgress();

                //2.得到startY,startX
                startY = event.getY();
                startX = event.getX();
                //3.计算屏幕的高
                touchRang = Math.min(screenHeight, screenWidth);
                touchWidth = Math.max(screenHeight,screenWidth);

                handler.removeMessages(HIDE_MEDIACONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE:

                //4.endY,endX
                float endY = event.getY();
                float endX = event.getX();
                //5.计算偏移量
                float distanceY = startY - endY;
                //计算X轴的偏移量
                float distanceX = endX - startX;
                //6.改变的声音 = （在屏幕上滑动的距离/屏幕的高）*最大音量
                float delta = (distanceY/touchRang)*maxVolume;
                //改变的视频进度
                float delVideo = (distanceX/touchWidth)*duration;
                //最终音量=原来的音量+改变的声音
                int volume = (int)Math.min(Math.max(mVol + delta,0),maxVolume);
                int duration1 = (int) Math.min(Math.max(mProgress+delVideo,0),duration);
                //x轴改变的距离
                int totalX = Math.abs(eventX - downX);
                int totalY = Math.abs(eventY - downY);

                 //当Y轴的距离大于8的时候再进行增加，减少声音
                if(delta != 0 && totalY > 20) {
                    updataVolumeProgress(volume);
                }
                //当x轴的距离大于8的时候再进行快进，快退
                if(duration1 != 0 && totalX > 20) {
                    updataVideoProgress(duration1);
                }
                break;
            case MotionEvent.ACTION_UP:

                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updataVideoProgress(int duration1) {
        //调节视频进度
        seekbarVideo.setProgress(duration1);
        //将视频的位置设置到最终的点
        videoview.seekTo(duration1);
        //改变左边显示的视频时间
        tvCurrenttime.setText(utils.stringForTime(duration1));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updataVolumeProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            updataVolumeProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //从列表播放
            MediaItem mediaItem = mediaItems.get(position);
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
        } else if (uri != null) {
            //文件夹
            videoview.setVideoURI(uri);
        }
        setButtonStatus();
    }

    private void setListener() {

        //设置三个监听，准备好了，播放出错，播放完成
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                Log.e("TAG", "videoWidth" + videoWidth);
                Log.e("TAG", "videoHeight" + videoHeight);
                videoview.start();//开始播放
                //得到视频的总时长
                duration = videoview.getDuration();
                seekbarVideo.setMax(duration);
                //设置总时长
                tvDuration.setText(utils.stringForTime(duration));
                //发消息更新seekbar
                handler.sendEmptyMessage(PROGRESS);

                hideMediaController();
                //默认屏幕播放
                setVideoType(SCREEN_DEFAULT);
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();//退出播放器
            }
        });

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //设置视频的拖动
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *  当手指在拖动改变进度的时候回调这个方法
             * @param seekBar
             * @param progress
             * @param fromUser 如果是用户行为为true，否则为false
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    videoview.seekTo(progress);
                }
            }

            //当手指开始触摸的时候回调这个方法
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                handler.removeMessages(HIDE_MEDIACONTROLLER);

            }

            //当手指离开的时候回调这个方法
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 3000);
            }
        });

        //设置拖动seekbar改变声音
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updataVolumeProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
            }
        });
    }

    private void updataVolumeProgress(int progress) {
        //调节声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        //设置seekbar的进度
        seekbarVoice.setProgress(progress);
        currentVolume = progress;
        //是否静音
        if(progress<=0) {
            isMute = true;
        }else {
            isMute = false;
        }
    }

    //判断是否显示控件面板
    private void hideMediaController() {

        if (!isShowMediaController) {
            //隐藏控件面板
            llBottom.setVisibility(View.GONE);
            llTop.setVisibility(View.GONE);
        } else {
            //显示控件面板
            llBottom.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.VISIBLE);
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新进度
                case PROGRESS:

                    int progress = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(progress);
                    tvCurrenttime.setText(utils.stringForTime(progress));
                    //更新系统时间
                    tvSysytemTime.setText(getSystemTime());

                    //移除之前的消息，重复发送消息
                    removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    isShowMediaController = false;
                    hideMediaController();
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        return format.format(new Date());
    }

    private void getData() {
        //播放本地文件
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    @OnClick({R.id.btn_voice, R.id.btn_switch_player, R.id.btn_exit, R.id.btn_pre, R.id.btn_play_pause, R.id.btn_next, R.id.btn_switch_screen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                isMute = !isMute;
                updateVolume(currentVolume);
                break;
            case R.id.btn_switch_player:
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_pre:
                //播放上一个视频
                playPreVideo();
                break;
            case R.id.btn_play_pause:
                startAndPause();
                break;
            case R.id.btn_next:
                //播放下一个视频
                playNextVideo();
                break;
            case R.id.btn_switch_screen:
                if (isFullScreen) {
                    //默认
                    setVideoType(SCREEN_DEFAULT);
                } else {
                    //全屏
                    setVideoType(SCREEN_FULL);
                }
                break;
        }
    }

    private void updateVolume(int progress) {
        if(isMute) {
            //是静音，调节声音
             audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            //设置seekBar进度
            seekbarVoice.setProgress(0);
        }else {
            //调节声音
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            //设置seekbar的进度
            seekbarVoice.setProgress(progress);
            currentVolume= progress;
        }
    }

    private void setVideoType(int screenDefault) {
        switch (screenDefault) {
            case SCREEN_DEFAULT:
                isFullScreen = false;
                //真实的视频的款和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //计算后的视频款和高的结果，默认和屏幕的一样

                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoview.setVideoSize(width, height);

                //设置按钮为全屏
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);

                break;
            case SCREEN_FULL:

                isFullScreen = true;
                //设置全屏
                videoview.setVideoSize(screenWidth, screenHeight);
                //设置按钮为默认
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);

                break;
        }
    }

    private void playPreVideo() {

        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                videoview.setVideoPath(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                btnPlayPause.setBackgroundResource(R.drawable.btn_pause_selector);
                setButtonStatus();
            }
        }
    }

    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                videoview.setVideoPath(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                btnPlayPause.setBackgroundResource(R.drawable.btn_pause_selector);
                setButtonStatus();

                if (position == mediaItems.size() - 1) {
                    Toast.makeText(this, "这是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
            } else {
                finish();
            }
        }
    }

    private void setButtonStatus() {
        if (mediaItems != null && mediaItems.size() > 0) {
            setEnable(true);
            if(position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }
            if(position == mediaItems.size()-1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        } else if (uri != null) {
            //播放一个地址
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable) {
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
        } else {
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
        }
        btnNext.setEnabled(isEnable);
        btnPre.setEnabled(isEnable);
    }

    private void startAndPause() {
        if (videoview.isPlaying()) {
            //点击后暂停播放
            handler.removeMessages(PROGRESS);
            videoview.pause();
            //按钮的图标改变
            btnPlayPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            //点击后开始播放
            videoview.start();
            handler.sendEmptyMessage(PROGRESS);
            //按钮设置-暂停状态
            btnPlayPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    @Override
    protected void onDestroy() {
        if(receiver != null) {
            //移除注册的广播
            unregisterReceiver(receiver);
           receiver = null;
        }
        //移除所有消息
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);

        super.onDestroy();

    }
}
