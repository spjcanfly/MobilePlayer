package com.example.spj.mobileplayer.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.AudioPlayerActivity;
import com.example.spj.mobileplayer.adapter.VideoFragmentAdapter;
import com.example.spj.mobileplayer.base.BaseFragment;
import com.example.spj.mobileplayer.domain.MediaItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by spj on 2016/9/6.
 */
public class AudioFragment extends BaseFragment {


    private ArrayList<MediaItem> mediaItems;
    private VideoFragmentAdapter adapter;

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_namedia)
    TextView tvNamedia;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有音频
                tvNamedia.setVisibility(View.GONE);
                adapter = new VideoFragmentAdapter(mContext, mediaItems,false);
                listview.setAdapter(adapter);
            } else {
                //没有音频
                tvNamedia.setVisibility(View.VISIBLE);
                tvNamedia.setText("没有找到音频文件");
            }
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_video, null);
        ButterKnife.bind(this, view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MediaItem mediaItem = mediaItems.get(position);
                //传递音频列表
                Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //加载本地音频
        getData();
    }

    private void getData() {
        //耗时操作，开启子线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();

                ContentResolver resolver = mContext.getContentResolver();
                //获得音频的uri
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = new String[]{
                        //音频的各个字段
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST,
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem item = new MediaItem();
                        String name = cursor.getString(0);
                        item.setName(name);
                        long size = cursor.getLong(1);
                        item.setSize(size);
                        long duration = cursor.getLong(2);
                        item.setDuration(duration);
                        String data = cursor.getString(3);
                        item.setData(data);
                        String artist = cursor.getString(4);
                        item.setArtist(artist);
                        //将每一个对象放入集合中
                        mediaItems.add(item);
                    }
                    //关闭cursor
                    cursor.close();
                }
                //准备好音频后发送消息
                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
