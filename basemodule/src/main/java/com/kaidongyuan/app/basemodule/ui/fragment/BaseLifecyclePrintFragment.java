package com.kaidongyuan.app.basemodule.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaidongyuan.app.basemodule.widget.MLog;

public class BaseLifecyclePrintFragment extends BaseImplFragment {
    String className;
    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        MLog.i(className + " onInflate");
    }

    @Override
    public void onAttach(Activity activity) {
        className = getClass().getSimpleName();
        super.onAttach(activity);
        MLog.i(className + " onAttach");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MLog.i(className + " onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MLog.i(className + " onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        MLog.i(className + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        MLog.i(className + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MLog.i(className + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        MLog.i(className + " onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.i(className + " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MLog.i(className + " onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLog.i(className + " onDestroyView");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.i(className + " : " + (hidden ? "invisible" : "visible"));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MLog.i(className + " onHiddenChanged");
    }
}
