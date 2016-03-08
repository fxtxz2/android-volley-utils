package com.zyl.androidvolleyutils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * multipart/form-data的POST方式HTTP请求(只支持简单的String)
 * @author zyl
 */
public class MultipartRequest<T> extends Request<T> {
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
	private final Listener<T> listener;

	/**
	 * 消息实体
	 */
	private MultipartEntity entity = new MultipartEntity();

	/**
	 * 
	 * @param method HTTP 请求方法类型
	 * @param url  HTTP 请求URL
	 * @param clazz 解析json类
	 * @param headers 请求头
	 * @param params multipart/form-data请求参数
	 * @param listener 交付监听
	 * @param errorListener 错误监听
	 * @throws UnsupportedEncodingException 请求参数编码异常
	 */
	public MultipartRequest(int method, String url, Class<T> clazz,
			Map<String, String> headers, Map<String, String> params,
			Listener<T> listener, ErrorListener errorListener)
			throws UnsupportedEncodingException {
		super(method, url, errorListener);
		this.clazz = clazz;
		this.headers = headers;
		this.listener = listener;

		for (Map.Entry<String, String> entry : params.entrySet()) {
			entity.addStringPart(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public byte[] getBody() throws AuthFailureError {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
			return bos.toByteArray();// TODO OOM
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.getBody();
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
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
			Log.v("json", json);
			return Response.success(JSON.parseObject(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}
}
