package com.example.spj.mobileplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.MainActivity;
import com.example.spj.mobileplayer.domain.MediaItem;
import com.example.spj.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by spj on 2016/9/6.
 */
public class VideoFragmentAdapter extends BaseAdapter {
    private  Context mContext;
    private final ArrayList<MediaItem> mediaItems;
    private final Utils utils;
    private boolean isVideo = true;
    //加载封面图片,列表的位置
    private int pos=-1;

    public VideoFragmentAdapter(Context mContext, ArrayList<MediaItem> mediaItems,boolean isVideo) {
        this.mContext = mContext;
        this.mediaItems = mediaItems;
        utils =Utils.getInstance();
        this.isVideo = isVideo;
    }

    @Override
    public int getCount() {
        return mediaItems == null ? 0 : mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            convertView= View.inflate(mContext, R.layout.item_video_fragment, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        }else {
             viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据位置得到数据
        final MediaItem mediaItem = mediaItems.get(position);
        String data = mediaItem.getData();//视频的绝对地址
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));


        if(isVideo) {
            //加载视频的缩略图
            loadImage(data,viewHolder.iv_icon);
        }else {

//            if(position == pos) {
//                viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
//            }else {
//                Bitmap bitmap = Utils.getArtworkFromFile(mContext, mediaItem.getId(), mediaItem.getAlbum_id());
//                viewHolder.iv_icon.setImageBitmap(bitmap);
//            }
            //设置音乐专辑的图片
            Uri selectedAudio = Uri.parse(mediaItem.getData());
            MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
            myRetriever.setDataSource(mContext, selectedAudio); // the URI of audio file
            byte[] artwork;

            artwork = myRetriever.getEmbeddedPicture();

            if (artwork != null) {
                Bitmap bMap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
                viewHolder.iv_icon.setImageBitmap(bMap);

            } else {
                viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);

            }
        }


        return convertView;
    }

    private void loadImage(final String data,final ImageView iv_icon) {

        new Thread(){
            @Override
            public void run() {
                super.run();
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(mContext, Uri.parse(data));

                final Bitmap bitmap = metadataRetriever.getFrameAtTime();
                ((MainActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_icon.setImageBitmap(bitmap);
                    }
                });

            }
        }.start();
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}
