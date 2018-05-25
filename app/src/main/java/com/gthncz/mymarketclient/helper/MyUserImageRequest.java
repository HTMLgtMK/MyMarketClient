package com.gthncz.mymarketclient.helper;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.gthncz.mymarketclient.ClientApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GT on 2018/5/21.
 */

public abstract class MyUserImageRequest extends ImageRequest {

    public MyUserImageRequest(String url,
                              Response.Listener<Bitmap> listener,
                              int maxWidth,
                              int maxHeight,
                              ImageView.ScaleType scaleType,
                              Bitmap.Config decodeConfig,
                              Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> map = new HashMap<>();
        map.put("XX-Token", getUserToken());
        map.put("XX-Device-Type", "android");
        return map;
    }

    /*抽象方法, 用于传入用户Token*/
    public abstract String getUserToken();
}
