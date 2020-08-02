package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class AnimationUtil {
   public static RotateAnimation createRotateAnimation(int rotateangle,long duration,boolean fillAfter) {
       RotateAnimation ra = new RotateAnimation(0, rotateangle,
               Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
               0.5f);// 设置锚点为图片的中心点
       // 设置动画播放时间
       ra.setDuration(duration);
       ra.setFillAfter(fillAfter);// 动画播放完之后，是否停留在当前状态
       return ra;
   }
    public static AlphaAnimation createAlphaAnimation(float fromAlpha,float toAlpha,long duration,boolean fillAfter) {
        // 渐变动画
        AlphaAnimation aa = new AlphaAnimation(fromAlpha, toAlpha);// 由完全透明到不透明
        // 设置动画播放时间
        aa.setDuration(duration);
        aa.setFillAfter(fillAfter);// 动画播放完之后，是否停留在当前状态
        return aa;
    }
    public static ScaleAnimation createScaleAnimation(float fromXY,float toXY,long duration,boolean fillAfter) {
        // 缩放动画
        ScaleAnimation sa = new ScaleAnimation(fromXY, toXY, fromXY, toXY,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);// 设置锚点为图片的中心点
        // 设置动画播放时间
        sa.setDuration(duration);
        sa.setFillAfter(fillAfter);// 动画播放完之后，是否停留在当前状态
        return sa;
    }



}
