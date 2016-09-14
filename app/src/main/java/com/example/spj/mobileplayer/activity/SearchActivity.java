package com.example.spj.mobileplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.mobileplayer.R;
import com.example.spj.mobileplayer.adapter.SearchAdapter;
import com.example.spj.mobileplayer.domain.SearchBean;
import com.example.spj.mobileplayer.speech.JsonParser;
import com.example.spj.mobileplayer.utils.Constants;
import com.example.spj.mobileplayer.utils.LogUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends Activity {

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private List<SearchBean.ItemsEntity> items;

    private SearchAdapter adapter;

    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.iv_voice)
    ImageView ivVoice;
    @Bind(R.id.tv_search)
    TextView tvSearch;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

//        setListener();
    }

//    private void setListener() {
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                //3.传递视频列表
//                Intent intent = new Intent(SearchActivity.this, VitamioPlayerActivity.class);
//
//                //传递列表
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("videolist", (Serializable) items);
//                intent.putExtras(bundle);
////                intent.putExtra("isNews",true);
//
//                //传递位置
//                intent.putExtra("position", position);
//                startActivity(intent);
//            }
//        });
//    }

    @OnClick({R.id.iv_voice, R.id.tv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_voice:
                showVocieDialog();
                break;
            case R.id.tv_search:
                goSearch();
                break;
        }
    }



    private void goSearch() {

        String text = etSearch.getText().toString().trim();
        try {
            //汉字 转换为 %E6%AF%9B%E4%B8%BB%E5%B8%AD
            text = URLEncoder.encode(text,"UTF-8");
            String url = Constants.SEARCH_URL + text;
            getDataFromNet(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromNet(String url) {
        progressbar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                //解析数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String result) {
        SearchBean bean = parsedJson(result);
        items = bean.getItems();
        if(items != null && items.size()>0) {
            //有数据
            tvResult.setVisibility(View.GONE);
            adapter = new SearchAdapter(SearchActivity.this,items);
            //设置适配器
            listview.setAdapter(adapter);
        }else {
            //没有数据
            tvResult.setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);
    }

    private SearchBean parsedJson(String result) {
        return new Gson().fromJson(result,SearchBean.class);
    }

    private void showVocieDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
        mDialog.setParameter(SpeechConstant.DOMAIN, "iat");//日常用语
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化失败了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyRecognizerDialogListener implements RecognizerDialogListener {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        String reuslt = resultBuffer.toString();
        reuslt = reuslt.replace("。", "");
        etSearch.setText(reuslt);
        etSearch.setSelection(etSearch.length());
    }
}
