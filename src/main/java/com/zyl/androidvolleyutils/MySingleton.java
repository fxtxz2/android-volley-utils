package com.zyl.androidvolleyutils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.zyl.androidlruimagecache.LruImageCache;

import java.net.Proxy;
import java.util.Map;

/**
 * Here is an example of a singleton class that provides RequestQueue and ImageLoader functionality
 * 
 * 简单的使用：
 * 
 * @author zyl
 *
 */
// 单例模式需要优化
public class MySingleton {
    /**
     * 请求队列
     */
	private RequestQueue mRequestQueue;
    /**
     * Image Loader
     */
	private ImageLoader mImageLoader;
    /**
     * 当前上下文
     */
	private static Context mCtx;

    /**
     * 公共请求头
     */
    Map<String, String> headerMap;
    /**
     * 代理
     */
    private Proxy proxy;

    /**
     * 是否要合并重复header头
     */
    private boolean isDuplicateHeader = false;

    private static class MySingletomHolder{
        private static final MySingleton INSTANCE = new MySingleton();
    }
    private MySingleton(){}

    public static MySingleton getInstance() {
        return MySingletomHolder.INSTANCE;
    }


    /**
     * 初始化
     * @param context 当前上下文
     */
    public void init(Context context) {
        init(context, null, null);
    }

    /**
     * 初始化
     * @param context 当前上下文
     * @param headerMap 请求头
     */
    public void init(Context context, Map<String, String> headerMap) {
        init(context, headerMap, null);
    }

    /**
     * 初始化
     * @param context 当前上下文
     * @param proxy 代理设置
     */
    public void init(Context context,  Proxy proxy) {
        init(context,null,proxy);
    }
    /**
     * 初始化
     * @param context 当前上下文
     * @param headerMap 请求头
     * @param proxy 代理
     */
	public void init(Context context, Map<String, String> headerMap, Proxy proxy) {
        mCtx = context;
        this.headerMap = headerMap;
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("192.168.198.40", 2386));
        this.proxy = proxy;
        mRequestQueue = getRequestQueue();
        /**
         *  取运行内存阈值的1/8作为图片缓存
         */
        final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) mCtx
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8;

        Log.v("MEM_CACHE_SIZE", String.valueOf(MEM_CACHE_SIZE));
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
        	// 使用自己的LRU的bitmap缓冲
//            private final LruBitmapCache
//            	cache = new LruBitmapCache(20);
//            private final ImageLreCache cache = new ImageLreCache(MEM_CACHE_SIZE,"images",10*1024*1024);
            private final LruImageCache cache = new LruImageCache(MEM_CACHE_SIZE,"images",30*1024*1024, mCtx);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }
	
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
//            mRequestQueue = MyVolley.newRequestQueue(mCtx.getApplicationContext());

            mRequestQueue = MyVolley.newRequestQueue(mCtx.getApplicationContext(), new OkHttpStack(proxy), headerMap);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public boolean isDuplicateHeader() {
        return isDuplicateHeader;
    }

    public void setDuplicateHeader(boolean duplicateHeader) {
        isDuplicateHeader = duplicateHeader;
    }
}
