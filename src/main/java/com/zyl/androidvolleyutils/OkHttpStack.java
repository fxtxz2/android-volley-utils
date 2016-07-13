package com.zyl.androidvolleyutils;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.OkHttpURLConnection;
import okhttp3.internal.huc.OkHttpsURLConnection;

/**
 * An @see com.android.volley.toolbox.HttpStack implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {
    private final OkHttpClient client;
    private Proxy proxy;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.client = client;
    }

    public OkHttpStack(Proxy proxy) {
        this(new OkHttpClient());
        this.proxy = proxy;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        String protocol = url.getProtocol();
        if (proxy == null){
            proxy = client.proxy();
        }

        OkHttpClient copy = client.newBuilder()
                .proxy(proxy)
                .build();

        if (protocol.equals("http")) return new OkHttpURLConnection(url, copy);
        if (protocol.equals("https")) return new OkHttpsURLConnection(url, copy);
        throw new IllegalArgumentException("Unexpected protocol: " + protocol);
    }
}
