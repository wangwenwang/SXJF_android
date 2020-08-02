package com.kaidongyuan.app.kdydriver.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可开关滑动的Viewppager
 */
public class IsScrollableViewPager extends ViewPager {
    private boolean isScroll = false;//默认为禁止滑动
    public IsScrollableViewPager(Context context) {
        super(context);

    }

    public IsScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * @param isScroll 可滑动开关
     */
    public void setisScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScroll){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScroll&&this.getCurrentItem()<=this.getChildCount()&&this.getCurrentItem()>=0){
            getParent().requestDisallowInterceptTouchEvent(true);//取消父视图对触摸事件的拦截，用于双层嵌套滑动事件冲突等情况
            return super.onTouchEvent(ev);
        }else {
            //禁止滑动，返回false,触摸事件不消费掉
            return false;
        }
    }

}
