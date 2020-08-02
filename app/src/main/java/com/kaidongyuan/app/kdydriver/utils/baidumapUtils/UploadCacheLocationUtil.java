package com.kaidongyuan.app.kdydriver.utils.baidumapUtils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.bean.order.LocationContineTime;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.utils.LocationFileHelper;
import com.kaidongyuan.app.kdydriver.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传缓存位置的工具类
 */
public class UploadCacheLocationUtil {

    private static String Tag_Upload_PositionList = "Tag_Upload_PositionList";
    private static List<LocationContineTime>[] mLocationArr;
    private static String mUserId;
    private static RequestQueue mRequestQueue;
    private static int mUploadSize= 20;
    private static int mUploadIndex = 0;

    /**
     * 将缓存位置点集合分批上传到服务器
     * @param mContext
     * @param requestQueue
     * @param userId
     * @param locationContineTimeList 要上传的缓存位置点位集合
     */
    public static void uploadCacheLocation(Context mContext, RequestQueue requestQueue, final String cacheFileName, String userId, List<LocationContineTime> locationContineTimeList){
        mUserId = userId;
        mRequestQueue = requestQueue;
        mUploadIndex=0;
        //将缓存位置信息集合中的点位信息分成二十条一次发送，发送完成后再发送二十条
        // 每次发送完就将缓存文件中的数据清除二十条
        int size = locationContineTimeList.size()/mUploadSize + 1;
        mLocationArr = new List[size];

        for (int i=0; i<size; i++) {
            if (i==(size-1)) {
                mLocationArr[i] = locationContineTimeList.subList(i*mUploadSize, locationContineTimeList.size());
            } else {
                mLocationArr[i] = locationContineTimeList.subList(i*mUploadSize, (i+1) * mUploadSize);
            }
            MLog.w("UploadCacheLocationUtil.mLocationArr["+i+"]:"+mLocationArr[i]);
        }
        uploadCache(mContext, cacheFileName);
    }


    /**
     * 上传缓存位置点位信息
     * @param mContext
     * @param cacheFileName
     */
    public static void uploadCache(final Context mContext, final String cacheFileName){
        try {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext);
            }
            String url = Constants.URL.CurrentLocationList;
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MLog.w(response);
                    com.alibaba.fastjson.JSONObject jo = JSON.parseObject(response);
                    int type = Integer.parseInt(jo.getString("type"));
                    if (type>=1) {
                        //上传成功，将缓存文件中的位置点位信息删除
                        LocationFileHelper.deleteLocation(mLocationArr[mUploadIndex].size(), cacheFileName);
                        MLog.w( "UploadCacheLocationUtil.uploadCacheLocation.上传缓存数据成功 time：" + DataUtil.getStringTime(System.currentTimeMillis()) + response);
                        mUploadIndex ++;
                        if (mUploadIndex<mLocationArr.length){
                            uploadCache(mContext, cacheFileName);
                        }
                    } else {
                        MLog.w( "UploadCacheLocationUtil.uploadCacheLocation.上传缓存数据失败：" + response);
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //error.networkResponse.statusCode 存在NullPointerException异常
                    MLog.w(  "UploadCacheLocationUtil.uploadCacheLocation.error 网络原因导致上传缓存失败");
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    if (mUserId == null || mUserId.equals("")) {
                        mUserId = SharedPreferencesUtils.getUserId();
                    }
                    params.put("strUserIdx", mUserId);
                    //  List<LocationContineTime>locationContineTimes=mLocationArr[mUploadIndex];
                    JSONArray jsonArr = LocationPointUtil.changeLocationListToString(mLocationArr[mUploadIndex]);
                    JSONObject object = new JSONObject();
                    object.put("result", jsonArr);
                    String uploadLocations = object.toString();
                    params.put("strJson",uploadLocations+"");
                    params.put("strLicense", "");
                    MLog.w( "UploadCacheLocationUtil.uploadCacheLocation.开始上传缓存数据.strJson:"+uploadLocations);
                    return params;
                }
            };
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 1, 2.0f));
            mStringRequest.setTag(Tag_Upload_PositionList);
            mRequestQueue.add(mStringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
























