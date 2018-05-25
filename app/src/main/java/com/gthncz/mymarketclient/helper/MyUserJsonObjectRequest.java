package com.gthncz.mymarketclient.helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gthncz.mymarketclient.ClientApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 与用户操作相关的网络请求
 * Created by GT on 2018/5/11.
 */

public abstract class MyUserJsonObjectRequest extends JsonObjectRequest {

    public MyUserJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public MyUserJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    /**
     * 重写此方法, 传入Header信息
     * @return header map
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> map = new HashMap<>();
        map.put("XX-Token", getUserToken());
        map.put("XX-Device-Type", "android");
        return map;
    }

    public abstract String getUserToken();
}
