package com.example.spj.mobileplayer.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.spj.mobileplayer.base.BaseFragment;

/**
 * Created by spj on 2016/9/6.
 */
public class NetVideoFragment extends BaseFragment{

    private TextView textView;

    @Override
    public View initView() {
        textView = new TextView(mContext);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("网络视频的内容");
    }
}
