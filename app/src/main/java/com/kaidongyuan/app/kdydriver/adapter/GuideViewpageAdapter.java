package com.kaidongyuan.app.kdydriver.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class GuideViewpageAdapter extends PagerAdapter {
    Context mcontext;
    ArrayList<ImageView>mimageViews;
    public GuideViewpageAdapter(Context context, ArrayList<ImageView> imageViews) {
        mcontext=context;
        mimageViews=imageViews;
    }
    public  void setMimageViews(ArrayList<ImageView> imageViews){
        mimageViews=imageViews;
    }

    public ArrayList<ImageView> getMimageViews() {
        return mimageViews;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mimageViews.get(position));

        return mimageViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mimageViews.get(position));
    }

    @Override
    public int getCount() {
        if (mimageViews!=null){
            return mimageViews.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
