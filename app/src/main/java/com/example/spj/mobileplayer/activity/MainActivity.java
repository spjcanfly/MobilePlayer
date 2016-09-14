package com.example.spj.mobileplayer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.base.BaseFragment;
import com.example.spj.mobileplayer.fragment.AudioFragment;
import com.example.spj.mobileplayer.fragment.NetAudioFragment;
import com.example.spj.mobileplayer.fragment.NetVideoFragment;
import com.example.spj.mobileplayer.fragment.VideoFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ArrayList<BaseFragment> baseFragments;
    private  Fragment mContent;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGrantExternalRW(this);

        initView();

        initFragment();

        setListener();
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(MainActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(

                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
            }, 1);

            return false;
        }

        return true;
    }

    private void setListener() {
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //默认选择第一个页面
        rg_main.check(R.id.rb_local_video);
    }

    private void initFragment() {
        baseFragments = new ArrayList<>();
        baseFragments.add(new VideoFragment());
        baseFragments.add(new AudioFragment());
        baseFragments.add(new NetVideoFragment());
        baseFragments.add(new NetAudioFragment());
    }

    private void initView() {
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {



        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

            position = 0;


            switch (checkedId) {
                case R.id.rb_local_video :
                   position =0;
                    break;
                case R.id.rb_local_audio :
                    position =1;
                    break;
                case R.id.rb_net_video :
                    position =2;
                    break;
                case R.id.rb_net_audio :
                    position =3;
                    break;
            }
            //根据位置从集合中取出对应的Fragment
            Fragment toFragment = getFragment(position);

            //把对应的Fragment绑定到Activity中
            switchFragment(mContent,toFragment);

        }
    }

    //刚才显示过的Fragment隐藏，马上要显示的显示，添加
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
        if(mContent != toFragment) {
            mContent = toFragment;

            if(toFragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //判断toFragment是否添加
                if(toFragment.isAdded()) {
                    //添加了，隐藏fromFragment
                    if(fromFragment != null) {
                        ft.hide(fromFragment);
                    }
                    //显示toFragment,提交
                    ft.show(toFragment).commit();
                }else{
                    //没有添加，隐藏fromFragment，
                    if(fromFragment != null) {
                        ft.hide(fromFragment);
                    }
                    //添加toFragment，显示，提交
                    ft.add(R.id.fl_content,toFragment).commit();
                }
            }
        }
    }

    private BaseFragment getFragment(int position) {

        if(baseFragments != null && baseFragments.size()>0) {
            return baseFragments.get(position);
        }
        return null;
    }

    private  boolean isExit=true;
    private Handler handler = new Handler();


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(position != 0) {
                rg_main.check(R.id.rb_local_video);
                return true;
            }else {
                if(isExit) {
                    isExit = false;
                    Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isExit = true;
                        }
                    },2000);
                    return true;
                }

            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
