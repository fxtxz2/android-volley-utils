/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zyl.androidvolleyutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;

import java.io.File;
import java.util.Map;

public class MyVolley {
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and call @see RequestQueue#start() on it.
     *
     * @param context A @see Context to use for creating the cache dir.
     * @param stack   An @see HttpStack to use for the network, or null for
     *                default.
     * @param headerMap 请求头
     * @param maxDiskCacheBytes 缓存大小
     * @return A started @see RequestQueue instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes,
                                               Map<String, String> headerMap) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

//        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName, 0);
//            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException ignored) {
            ignored.printStackTrace();
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new MyHurlStack();
            }
//            else {
//                // Prior to Gingerbread, HttpUrlConnection was unreliable.
//                // See:
//                // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
//            }
        }

        MyBasicNetworkVolley network = new MyBasicNetworkVolley(stack);
        network.setHeaderMap(headerMap);

        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
            // No maximum size specified
            queue = new RequestQueue(new MyDiskBasedCache(cacheDir), network);
        }
        else
        {
            // Disk cache size specified
            queue = new RequestQueue(new MyDiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls @see RequestQueue#start() on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A @see Context to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started @see RequestQueue instance.
     */
    public static RequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes, null);
    }

    /**
     * Creates a default instance of the worker pool and calls @see RequestQueue#start() on it.
     *
     * @param context A @see Context to use for creating the cache dir.
     * @param stack An @see HttpStack to use for the network, or null for default.
     * @return A started @see RequestQueue instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack)
    {
        return newRequestQueue(context, stack, -1, null);
    }


    /**
     * Creates a default instance of the worker pool and calls @see com.android.volley.RequestQueue#start() on it.
     * @param context A @see Context to use for creating the cache dir.
     * @return A started @see RequestQueue instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    /**
     * 初始化队列
     * @param context 当前上下文
     * @param stack okhttp实现
     * @param headerMap 头文件
     * @return 请求队列
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, Map<String, String> headerMap) {
        return newRequestQueue(context,  stack, -1, headerMap);
    }
}
