package com.kaidongyuan.app.kdydriver.serviceAndReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.igexin.sdk.PushConsts;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.interfaces.BaseActivityListener;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.httpclient.OrderAsyncHttpClient;
import com.kaidongyuan.app.kdydriver.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class GetuiPushReceiver  extends BroadcastReceiver  {
    private RequestQueue mRequestQueue;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        MLog.e("PushConsts.CMD_ACTION:"+bundle.getInt(PushConsts.CMD_ACTION));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                final String cid = bundle.getString("clientid");
                // TODO:处理cid返回
                MLog.e("clientid:"+cid);
                if (mRequestQueue==null){
                    RequestQueue mRequestQueue=Volley.newRequestQueue(context);
                    StringRequest request=new StringRequest(Request.Method.POST, Constants.URL.SavaPushToken, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            com.alibaba.fastjson.JSONObject jo = JSON.parseObject(response);
                            int type = Integer.parseInt(jo.getString("type"));
                            if(type>=1) {
                                MLog.w("上传CID与UserID成功");
                            }else {
                                MLog.w("上传失败，后台返回值type："+type);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            MLog.w("SavaPushToken网络上传失败");
                            error.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            String mUserId = SharedPreferencesUtils.getUserId();
                            params.put("strUserId", mUserId);
                            params.put("strCID", cid);
                            params.put("strToken", "");
                            params.put("strLicense", "");
                            MLog.i("params:"+params.toString());
                            return params;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(30*1000, 1, 1.0f));  // 设置超时
                    mRequestQueue.add(request);
                }

                break;
            case PushConsts.GET_MSG_DATA:

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    // TODO:接收处理透传（payload）数据
                    try {
                        JSONObject jo= JSON.parseObject(data);
                        MLog.e(jo.getString("key0"));
                    }catch (JSONException ep){
                        ep.printStackTrace();
                    }

                }
                break;
            case PushConsts.SETTAG_SUCCESS:
                //个推消息推送时，提示应用icon右上角小红点
                MLog.e("提示应用icon右上角小红点");
                ShortcutBadger.applyCount(context,1);
                break;
            default:
                break;
        }
    }


}
