package com.example.spj.mobileplayer.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.SystemPlayerActivity;
import com.example.spj.mobileplayer.adapter.VideoFragmentAdapter;
import com.example.spj.mobileplayer.base.BaseFragment;
import com.example.spj.mobileplayer.domain.MediaItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by spj on 2016/9/6.
 */
public class VideoFragment extends BaseFragment {

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_namedia)
    TextView tvNamedia;
    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                tvNamedia.setVisibility(View.GONE);
                //设置适配器
                adapter = new VideoFragmentAdapter(mContext, mediaItems, true);
                listview.setAdapter(adapter);
            } else {
                //没有数据
                tvNamedia.setVisibility(View.VISIBLE);
            }
        }
    };
    private VideoFragmentAdapter adapter;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_video, null);
        ButterKnife.bind(this, view);

        //设置item的点击事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                MediaItem mediaItem = mediaItems.get(position);
                //1.使用系统里的所有的播放器播放
//                Intent intent = new Intent();
//                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//                mContext.startActivity(intent);
                //2.使用自己写的播放器播放
                Intent intent = new Intent(mContext, SystemPlayerActivity.class);

                //传递列表
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);

                intent.putExtras(bundle);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getData();
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<MediaItem>();

                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//视频的uri8
                String[] objs = new String[]{
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件的名称
                        MediaStore.Video.Media.SIZE,//文件大小
                        MediaStore.Video.Media.DURATION,//视频文件的时长
                        MediaStore.Video.Media.DATA,//视频文件的绝对地址
                        MediaStore.Video.Media.ARTIST//艺术家
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
                //发送消息
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
