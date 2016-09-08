package com.example.spj.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by spj on 2016/9/7.
 */
public class VideoView extends android.widget.VideoView{
    public VideoView(Context context) {
        this(context,null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //自定义视频的宽和高
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width =videoWidth;
        l.height = videoHeight;
        setLayoutParams(l);

    }
}
