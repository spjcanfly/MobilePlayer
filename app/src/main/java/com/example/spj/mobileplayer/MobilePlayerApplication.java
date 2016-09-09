package com.example.spj.mobileplayer;

import android.app.Application;

import org.xutils.x;

/**
 * Created by spj on 2016/9/9.
 */
public class MobilePlayerApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
