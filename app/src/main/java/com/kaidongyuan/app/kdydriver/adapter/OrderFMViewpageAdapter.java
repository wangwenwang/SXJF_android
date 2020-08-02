package com.kaidongyuan.app.kdydriver.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaidongyuan.app.kdydriver.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFMViewpageAdapter extends FragmentPagerAdapter {
    Context mcontext;
    List<Fragment>fragments=new ArrayList<>();
    List<String>tablist=new ArrayList<>();
    public OrderFMViewpageAdapter(FragmentManager fm, List<Fragment> fragments, List<String>tablist , Context mcontext) {
        super(fm);
        this.fragments=fragments;
        this.tablist=tablist;
        this.mcontext=mcontext;
    }
//    @Override
//    public CharSequence getPageTitle(int position) {
//        Drawable drawable=mcontext.getResources().getDrawable(imagesrcs.get(position));
//        drawable.setBounds(0,0,drawable.getIntrinsicWidth()*2,drawable.getIntrinsicHeight()*2);
//        SpannableString sb=new SpannableString(tablist.get(position));
//        ImageSpan imageSpan=new ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
//    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    public View getTabView(String title){
        View view= LayoutInflater.from(mcontext).inflate(R.layout.tab_ispay_order,null);
        TextView textView= (TextView) view.findViewById(R.id.tv_tab_ispay_order);
        textView.setText(title);
        return view;
    }
}
