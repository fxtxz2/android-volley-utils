package com.zyl.androidvolleyutils;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.HttpURLConnectionImpl;
import okhttp3.internal.huc.HttpsURLConnectionImpl;

/**
 * An @see com.android.volley.toolbox.HttpStack implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {
    private final OkHttpClient client;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.client = client;
    }

    @Override protected HttpURLConnection createConnection(URL url) throws IOException {
        String protocol = url.getProtocol();
        Proxy proxy = client.proxy();
        OkHttpClient copy = client.newBuilder()
                .proxy(proxy)
                .build();

        if (protocol.equals("http")) return new HttpURLConnectionImpl(url, copy);
        if (protocol.equals("https")) return new HttpsURLConnectionImpl(url, copy);
        throw new IllegalArgumentException("Unexpected protocol: " + protocol);
    }
}
