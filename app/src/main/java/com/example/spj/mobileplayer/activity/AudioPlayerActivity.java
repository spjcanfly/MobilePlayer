package com.example.spj.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.mobileplayer.IMusicPlayerService;
import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.service.MusicPlayerService;
import com.example.spj.mobileplayer.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioPlayerActivity extends Activity {

    public static final int PROGRESS=1;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_artist)
    TextView tvArtist;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @Bind(R.id.btn_audio_playmode)
    Button btnAudioPlaymode;
    @Bind(R.id.btn_audio_pre)
    Button btnAudioPre;
    @Bind(R.id.btn_audio_play_pause)
    Button btnAudioPlayPause;
    @Bind(R.id.btn_audio_next)
    Button btnAudioNext;
    @Bind(R.id.btn_lyric)
    Button btnLyric;
    private int position;
    //false 来自列表，true，来自状态栏
    private boolean from;
    private IMusicPlayerService service;
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新进度
                case PROGRESS:

                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        //更新文本时间
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));
                        
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };
    
    
    //连接服务成功后回调这个方法
    private ServiceConnection con = new ServiceConnection() {

        /**
         *得到服务的代理类
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if(service != null) {
                if(!from) {
                    //来自于列表
                    try {
                        service.openAudio(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    //来自通知栏
                    try {
                        service.notifyChange(MusicPlayerService.OPEN_COMPLETE);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 当服务断开的时候调用这个方法
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private Utils utils;
    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        getData();

        bindAndStartService();

        setListener();


    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.mobilepalyer.OPENAUDIO");
        bindService(intent,con,Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void getData() {
        from = getIntent().getBooleanExtra("notification", false);

        if(!from) {

            position = getIntent().getIntExtra("position", 0);
        }

    }

    private void initView() {
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);

        //播放帧动画
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();

    }

    private void initData() {

        //注册监听广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);
        utils = Utils.getInstance();

    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(MusicPlayerService.OPEN_COMPLETE)) {
                //显示歌唱者，歌曲的名称
                showData();

                try {
                    int duration = service.getDuration();
                    seekbarAudio.setMax(duration);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                showPlayMode(false);

                //发消息更新进度
                handler.sendEmptyMessage(PROGRESS);

            }
        }

       

       
    }
    private void showPlayMode(boolean isShowToast) {
        int playmode=0;
        try {
            playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                //设置按钮的背景
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                }

                //tost
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                }

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
                }
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                if(isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    private void showData() {
        if (service != null) {
            try {
                tvArtist.setText(service.getArtist());
                tvName.setText(service.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.btn_audio_playmode, R.id.btn_audio_pre, R.id.btn_audio_play_pause, R.id.btn_audio_next, R.id.btn_lyric})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_playmode:
                changePlayMode();
                break;
            case R.id.btn_audio_pre:
                try {
                    service.pre();
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_play_pause:
                try {
                    if(service.isPlaying()) {
                        //点击后，暂停
                        service.pause();
                        //按钮变为播放
                        btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_selector);
                    }else {
                        //播放
                        service.start();
                        //按钮状态-暂停
                        btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_next:
                try {
                    service.next();
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_lyric:

                break;
        }
    }

    private void changePlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            }

            service.setPlaymode(playmode);//保持到Service里面

            //显示按钮的状态
            showPlayMode(true);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        if(con != null) {
            unbindService(con);
            con =null;
        }

        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
