package com.example.spj.mobileplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.SearchActivity;

/**
 * Created by spj on 2016/9/6.
 */
public class Titlebar extends LinearLayout implements View.OnClickListener {

    private final Context mContext;
    private View tv_search;
    private View rl_game;
    private View iv_history;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_history = getChildAt(3);

        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);

    }

    //在代码中创建的时候通常用这个方法
    public Titlebar(Context context) {
        this(context, null);
    }

    //在布局文件中使用该类的时候，系统通过这个构造方法实例化
    public Titlebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Titlebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search :
//                Toast.makeText(mContext, "搜索", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(mContext, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Toast.makeText(mContext, "历史记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
