package com.example.spj.mobileplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.activity.VitamioPlayerActivity;
import com.example.spj.mobileplayer.base.BaseFragment;
import com.example.spj.mobileplayer.domain.MediaItem;
import com.example.spj.mobileplayer.utils.CacheUtils;
import com.example.spj.mobileplayer.utils.Constants;
import com.example.spj.mobileplayer.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by spj on 2016/9/6.
 */
public class NetVideoFragment extends BaseFragment {

    private ArrayList<MediaItem> mediaItems;

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;
    private MyAdapter myAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_net_video, null);
        ButterKnife.bind(this, view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //3.传递视频列表
                Intent intent = new Intent(mContext, VitamioPlayerActivity.class);

                //传递列表
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtras(bundle);

                //传递位置
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getDataFromNet();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                getMoreDataFromNet();
            }
        });
        return view;
    }

    private void getMoreDataFromNet() {

        RequestParams reques = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(reques, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                processMoreData(result);
                refresh.finishRefreshLoadMore();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    private void processMoreData(String json) {
        mediaItems.addAll(parsedJson(json));
        myAdapter.notifyDataSetChanged();
    }

    private void getDataFromNet() {
        RequestParams request = new RequestParams(Constants.NET_VIDEO_URL);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                CacheUtils.putString(mContext, Constants.NET_VIDEO_URL, result);
                processData(result);
                refresh.finishRefresh();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {
        mediaItems = parsedJson(json);
        if (mediaItems != null && mediaItems.size() > 0) {
            //有视频
            tvNomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new MyAdapter();
            listview.setAdapter(myAdapter);
        } else {
            //没有视频
            tvNomedia.setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);
    }

    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray trailers = jsonObject.optJSONArray("trailers");
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject item = (JSONObject) trailers.get(i);
                if (item != null) {
                    MediaItem mediaitem = new MediaItem();

                    String name = item.optString("movieName");
                    mediaitem.setName(name);

                    String desc = item.optString("videoTitle");
                    mediaitem.setDesc(desc);

                    String imageUrl = item.optString("coverImg");
                    mediaitem.setImageUrl(imageUrl);

                    String data = item.optString("url");
                    mediaitem.setData(data);

                    mediaItems.add(mediaitem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    @Override
    public void initData() {
        super.initData();

        String saveJson = CacheUtils.getString(mContext, Constants.NET_VIDEO_URL);
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
    }


    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mediaItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null) {
                view = View.inflate(mContext, R.layout.item_net_video, null);

                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            //根据位置获得对应的数据
            MediaItem mediaItem = mediaItems.get(position);
            x.image().bind(viewHolder.ivIcon,mediaItem.getImageUrl());
            viewHolder.tvName.setText(mediaItem.getName());
            viewHolder.tvDesc.setText(mediaItem.getDesc());

            return view;
        }


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
