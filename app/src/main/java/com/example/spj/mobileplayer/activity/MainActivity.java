package com.example.spj.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initFragment();

        setListener();
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

             int position =0;


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
}
