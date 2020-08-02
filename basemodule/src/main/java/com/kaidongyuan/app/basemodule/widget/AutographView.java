package com.kaidongyuan.app.basemodule.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kaidongyuan.app.basemodule.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 签名并获取图片空间
 */
public class AutographView extends View {

    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Context mContext;
    private int mViewWidth, mViewHeight;
    private float currentX, currentY;
    private List<String> mData;
    private int mMessageTextSize;
    private int mMessageTextHeight;
    private int mSpanMessage;
    private float mBegainWrite;
    private int mMessageTextColor;
    private int mDrawTime=0;

    public AutographView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mPaint = new Paint();
        this.mData = new ArrayList<>();
        this.mMessageTextSize = dpToPx(mContext, 12);
        this.mMessageTextHeight = getTextHeight(mPaint, mMessageTextSize);
        this.mSpanMessage = dpToPx(mContext, 3);
        this.mMessageTextColor = Color.BLACK;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewWidth = w;
        this.mViewHeight = h;
        init();
    }

    /**
     * 初始化配置
     */
    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true); // 去除锯齿
        this.mPath = new Path();
        this.mBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_4444);
        this.mCanvas = new Canvas(mBitmap);
        this.mCanvas.drawColor(Color.WHITE);
        drawMessage();
        //设置签名是笔记的大小和风格
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制信息和签名框
     *  添加当前坐标的地址信息
     */
    public void drawMessage(){
        checkMessageTextSize();
        //绘制信息
        int count = mData.size();
        String message;
        Path messagePath;
        mPaint.setColor(mMessageTextColor);
        mPaint.setTextSize(mMessageTextSize);
        float messageLeft = dpToPx(mContext, 5);
        float messageTop = dpToPx(mContext, 5);
        float messageRight = mViewWidth;
        float messageBottom = messageTop;
//   本用于绘制 mData中的String内容， 被后面用于绘制地址信息
//        for (int i=0; i<count; i++) {
//            message = mData.get(i);
//            messagePath = new Path();
//            messageTop = messageBottom;
//            messageBottom = messageTop + mMessageTextHeight + mSpanMessage;
//            messagePath.addRect(messageLeft, messageTop, messageRight, messageBottom, Path.Direction.CW);
//            mCanvas.drawTextOnPath(message, messagePath, 0, mMessageTextSize, mPaint);
//        }
        //绘制签名框和时间
        int span = dpToPx(mContext, 3);
        float writeLeft = span;
        float writeTop = messageBottom + span*1;
        float writeRight = mViewWidth - span;
        float writeBottom = mViewHeight-span;
        //提示字样
        Path writePath = new Path();
        int warnTextSize = dpToPx(mContext, 14);
        mPaint.setTextSize(warnTextSize);
        mPaint.setColor(getResources().getColor(R.color.yb_yellow));
        writePath.addRect(writeLeft + span, writeTop + span, writeLeft+2*span+getTextWidth(mPaint,"在此区域签字：",warnTextSize), warnTextSize, Path.Direction.CW);
        mCanvas.drawTextOnPath("在此区域签字：", writePath, 0, warnTextSize, mPaint);
        //日期
        Path datePath = new Path();
        String dateStr = getCurrentTime();
        mPaint.setColor(Color.BLACK);
        //datePath.addRect(writeRight-2*span-getTextWidth(mPaint,dateStr,warnTextSize),writeTop+span,writeRight-span,warnTextSize,Path.Direction.CW);
        datePath.addRect(writeRight-2*span-getTextWidth(mPaint,dateStr,warnTextSize),mViewHeight - span*3 -2* getTextHeight(mPaint, warnTextSize),writeRight-span, mViewHeight - span*3 - getTextHeight(mPaint, warnTextSize),Path.Direction.CW);

        mCanvas.drawTextOnPath(dateStr,datePath,0,warnTextSize,mPaint);
        //地址信息
        Path adsPath=new Path();
        if (count>0&&!mData.get(0).isEmpty()){
            float dateLeft = mViewWidth - span*3 - getTextWidth(mPaint, mData.get(0), warnTextSize);
            float dateTop = mViewHeight - span*3 - getTextHeight(mPaint, warnTextSize);
            float dateRight = mViewWidth - span*2;
            float dateBottom = mViewHeight - span*2;
            mPaint.setColor(Color.BLACK);
            adsPath.addRect(dateLeft, dateTop, dateRight, dateBottom, Path.Direction.CW);
            mCanvas.drawTextOnPath(mData.get(0), adsPath, 0, warnTextSize, mPaint);
        }else{
            float dateLeft = mViewWidth - span*3 - getTextWidth(mPaint, "手机GPS地理定位失败", warnTextSize);
            float dateTop = mViewHeight - span*3 - getTextHeight(mPaint, warnTextSize);
            float dateRight = mViewWidth - span*2;
            float dateBottom = mViewHeight - span*2;
            mPaint.setColor(Color.BLACK);
            adsPath.addRect(dateLeft, dateTop, dateRight, dateBottom, Path.Direction.CW);
            mCanvas.drawTextOnPath("手机GPS地理定位失败", adsPath, 0, warnTextSize, mPaint);
        }

        //签名框
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.parseColor("#DC4717"));
        mCanvas.drawLine(writeLeft, writeTop, writeRight, writeTop, mPaint);
        mCanvas.drawLine(writeLeft, writeBottom, writeRight, writeBottom, mPaint);
        mCanvas.drawLine(writeLeft, writeTop, writeLeft, writeBottom, mPaint);
        mCanvas.drawLine(writeRight, writeTop, writeRight, writeBottom,mPaint);
        mBegainWrite = messageBottom;
    }

    private boolean shouldWrite = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y>mBegainWrite) {
                    currentX = x;
                    currentY = y;
                    mPath.moveTo(currentX, currentY);
                    shouldWrite = true;
                } else {
                    shouldWrite = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (y>mBegainWrite && shouldWrite) {
                    currentX = x;
                    currentY = y;
                    mPath.quadTo(currentX, currentY, x, y); // 画线
                } else {
                    shouldWrite = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (y>mBegainWrite && shouldWrite) {
                    mCanvas.drawPath(mPath, mPaint);
                } else {
                    shouldWrite = false;
                }
                break;
        }
        mDrawTime++;
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 设置数据
     * @param data 要显示的信息
     */
    public void setData(List<String> data) {
        this.mData = data==null? new ArrayList<String>():data;
    }

    /**
     * 设置文字大小
     * @param size 文字大小 dp
     */
    public void setMessageTextSize(int size){
        mMessageTextSize = dpToPx(mContext, size);
        mMessageTextHeight = getTextHeight(mPaint, mMessageTextSize);
    }

    /**
     * 设置信息字体颜色
     * @param color 颜色值 int 类型 16 进制
     */
    public void setMessageTextColor(int color){
        this.mMessageTextColor = color;
    }

    /**
     * 获取图片
     * @return 绘制后生成的图片
     */
    public Bitmap getPaintBitmap() {
        if (mDrawTime<=2) {
            return null;
        }
        return mBitmap;
    }

    /**
     * 清除画板
     */
    public void clear() {
        mDrawTime = 0;
        init();
        invalidate();
    }

    /**
     * 检测信息文字大小
     */
    private void checkMessageTextSize(){
        int size = mData.size();
        String message;
        String maxLongMessage = "";
        for (int i=0; i<size; i++) {
            message = mData.get(i);
            if (message.length()>maxLongMessage.length()) {
                maxLongMessage = message;
            }
        }
        while (getTextWidth(mPaint, maxLongMessage, mMessageTextSize)>mViewWidth) {
            mMessageTextSize = mMessageTextSize*9/10;
            mMessageTextHeight = getTextHeight(mPaint, mMessageTextSize);
        }
        int messageLimitHeight = mViewHeight*2/3;
        while (((mMessageTextHeight+mSpanMessage)*size+mSpanMessage)>messageLimitHeight) {
            mMessageTextSize = mMessageTextSize*9/10;
            mMessageTextHeight = getTextHeight(mPaint, mMessageTextSize);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context 上下文
     * @param dpValue 要转换的 dp 值
     * @return 转换后的 px 值
     */
    private int dpToPx(Context context, float dpValue) {
        if (context==null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据文字大小获取文字高度
     * @param paint 画笔
     * @param textSize 要绘制的文字的大小
     * @return 要绘制文字的高度
     */
    private int getTextHeight (Paint paint, int textSize){
        if (paint==null) {
            paint = new Paint();
        }
        String text = "测试";
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    /**
     * 根据文字大小获取文字宽度
     * @param paint 画笔
     * @param text 要绘制的文字
     * @param textSize 要绘制的文字的大小
     * @return 要绘制文字的宽度
     */
    private int getTextWidth (Paint paint, String text, int textSize){
        if (text!=null && text.length()>0) {
            if (paint==null) {
                paint = new Paint();
            }
            Rect rect = new Rect();
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), rect);
            return rect.width();
        }
        return 0;
    }

    /**
     * 获取当前时间
     * @return 时间字符串 “yyyy_MM_dd HH:mm:ss”
     */
    private String getCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\tHH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dataStr = dateFormat.format(date);
        return dataStr;
    }
}


















