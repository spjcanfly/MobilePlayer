package com.example.spj.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.spj.mobileplayer.domain.Lyric;
import com.example.spj.mobileplayer.utils.DensityUtils;

import java.util.ArrayList;

/**
 * Created by spj on 2016/9/12.
 */
public class ShowLyricView extends TextView{

    //文本的行高
    private int textHeight;
    private ArrayList<Lyric> lyrics;
    private Context context;
    private Paint paint;
    private Paint whitePaint;
    //高亮显示的时间
    private float sleepTime;
    //当前的播放进度
    private float currentPosition;
    //时间戳
    private float timePoint;
    //歌词索引
    private int index;
    private float width;
    private float height;


    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {

        textHeight = DensityUtils.dip2px(context, 20);
        //创建画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtils.dip2px(context, 22));
        //歌词居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        //设置抗锯齿
        whitePaint.setAntiAlias(true);
        whitePaint.setTextSize(DensityUtils.dip2px(context,16));
        //歌词居中对齐
        whitePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(lyrics != null && lyrics.size()>0) {
            //缓缓往上推移
            float plush = 0;
            if(sleepTime == 0) {
                plush = 0;
            }else {
                //这一句要移动的距离 = （这一句花的时间/这一句休眠时间） * 总距离(行高)
                //移动的在屏幕的最终坐标  = 行高 + 这一句要移动的距离
                plush = textHeight + ((currentPosition - timePoint)/sleepTime) * textHeight;
            }

            canvas.translate(0,-plush);

            //有歌词，绘制当前句
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent,width/2,height/2,paint);

            float tempY = height /2;

            //绘制前面部分
            for (int i = index-1; i >0 ; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                //当绘制到第一句后跳出该循环
                if(tempY <0) {
                    break;
                }
                canvas.drawText(preContent,width/2,tempY,whitePaint);
            }
            //因为上面的tempY的值已经到了0了，需要重新从一半的高开始
            tempY = height/2;
            //后面的部分
            for (int i = index+1; i <lyrics.size() ; i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if(tempY > height) {
                    break;
                }
                canvas.drawText(nextContent,width/2,tempY,whitePaint);
            }



        }else {
            //没有找到歌词
            canvas.drawText("没有找到歌词",width/2,height/2,paint);
        }
    }

    /**
     * 根据当前的播放进度同步歌词
     */
    public void setShowNextLyric(float currentPosition){
        this.currentPosition = currentPosition;
        //没有歌词，就不用绘制了
        if(lyrics == null || lyrics.size()==0) {
            return;
        }

        for (int i = 1; i < lyrics.size(); i++) {
            if(currentPosition < lyrics.get(i).getTimePoint()) {

                int tempIndex=i-1;

                if(currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    //得到当前该高亮显示的这句歌词的索引和时间戳和高亮时间
                    index = tempIndex;
                    timePoint = lyrics.get(index).getTimePoint();
                    sleepTime = lyrics.get(index).getSleepTime();
                }
            }
        }

        //重新绘制执行ondraw（）方法
        invalidate();//主线程
//        postInvalidate();//子线程绘制
    }

    //设置歌词列表
    public void setLyrics(ArrayList<Lyric> lyrics){
        this.lyrics = lyrics;
    }
}
