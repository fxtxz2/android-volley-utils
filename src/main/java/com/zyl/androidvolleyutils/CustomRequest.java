package com.zyl.androidvolleyutils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 普通的HTTP请求
 *
 * @author zyl
 */
public class CustomRequest<T> extends Request<T> {
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    /**
     * 解析类
     */
    private final Class<T> clazz;
    /**
     * 请求头
     */
    private final Map<String, String> headers;
    /**
     * 请求成功监听
     */
    private final Response.Listener<T> listener;

    /**
     * urlencoded请求参数
     */
    private final Map<String, String> params;

    private String requestUrl;
    private IGetNetData mGetNetData;// 获取网络原始数据后回调

    /**
     * @param method        HTTP 请求方法类型
     * @param url           HTTP 请求URL
     * @param clazz         解析json类
     * @param headers       请求头
     * @param listener      交付监听
     * @param errorListener 错误监听
     * @param params        application/x-www-form-urlencoded请求参数
     */
    public CustomRequest(int method, String url, Class<T> clazz,
                         Map<String, String> headers, Map<String, String> params,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.params = params;
        this.requestUrl = url;
        this.setShouldCache(false);
    }

    public CustomRequest(int method,String url,Class<T> clazz,Map<String, String> headers,Map<String, String> params, Response.Listener<T> listener, Response.ErrorListener errorListener, IGetNetData mIGetData) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.params = params;
        this.requestUrl = url;
        this.mGetNetData = mIGetData;// 回调接口
        this.setShouldCache(false);
    }

    /**
     * 增加的回调接口 获取网络原始数据后回调
     * 当前使用场景 : 任务大厅列表数据请求后 在此方法内缓存网络数据
     *
     * @author panyi
     */
    public interface IGetNetData {
        void handleNetDataCallBack(String url, String src);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            String json2 = new String(response.data, "UTF-8");
            Log.v("json", json);
            if (mGetNetData != null) {
                mGetNetData.handleNetDataCallBack(requestUrl, json);
            }
            return Response.success(JSON.parseObject(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 从Map中取得get形式的URL参数
     *
     * @param params 请求参数
     * @return get请求参数拼接
     */
    public static String getParamsFromMap(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append(URLEncoder.encode(entry.getKey(),
                        CustomRequest.DEFAULT_PARAMS_ENCODING));
                sb.append('=');
                sb.append(URLEncoder.encode(entry.getValue(),
                        CustomRequest.DEFAULT_PARAMS_ENCODING));
                sb.append('&');
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}//end class
