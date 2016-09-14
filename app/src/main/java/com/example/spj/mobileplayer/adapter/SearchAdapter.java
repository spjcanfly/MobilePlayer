package com.example.spj.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.domain.SearchBean;

import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by spj on 2016/9/13.
 */
public class SearchAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<SearchBean.ItemsEntity> items;

    public SearchAdapter(Context context, List<SearchBean.ItemsEntity> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            view = View.inflate(mContext, R.layout.item_net_video, null);
            viewHolder = new ViewHolder(view);
            viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            view.setTag(viewHolder);

        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        //根据位置获得对应的数据
        SearchBean.ItemsEntity mediaItem = items.get(i);
        x.image().bind(viewHolder.ivIcon,mediaItem.getItemImage().getImgUrl1());
        viewHolder.tvName.setText(mediaItem.getItemTitle());
        viewHolder.tvDesc.setText(mediaItem.getKeywords());

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView ivIcon;
        @Bind(R.id.rl_left)
        RelativeLayout rlLeft;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_desc)
        TextView tvDesc;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
