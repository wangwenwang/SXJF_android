package com.kaidongyuan.app.kdydriver.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaidongyuan.app.kdydriver.R;

import java.util.ArrayList;
import java.util.List;

public class MainViewpageAdapter extends FragmentPagerAdapter {
    Context mcontext;
    List<Fragment>fragments=new ArrayList<>();
    List<String>tablist=new ArrayList<>();
    List<Integer>imagesrcs=new ArrayList<>();

    public MainViewpageAdapter(FragmentManager fm, List<Fragment> fragments, List<String>tablist, List<Integer> imagesrcs , Context mcontext) {
        super(fm);
        this.fragments=fragments;
        this.tablist=tablist;
        this.imagesrcs=imagesrcs;
        this.mcontext=mcontext;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public View getTabView(String title,int src){
        View view= LayoutInflater.from(mcontext).inflate(R.layout.tab_main,null);
        TextView textView= (TextView) view.findViewById(R.id.tv_tab_main);
        ImageView imageView= (ImageView) view.findViewById(R.id.iv_tab_main);
        textView.setText(title);
        imageView.setImageResource(src);
        return view;
    }
}
