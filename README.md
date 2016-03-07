# android-volley-utils
volley基础配置，主要参考了[mcxiaoke/android-volley](https://github.com/mcxiaoke/android-volley)和[google官方的volley教程](http://developer.android.com/training/volley/requestqueue.html)实现了单列队列，可以添加公共的header,volley队列使用[OKHttp](http://square.github.io/okhttp/)作为HurlStack，支持multipart/form-data的POST方式HTTP请求。

# 指南
## 初始化和添加请求头
```Java
public class Application{
  @Override
   public void onCreate() {
     super.onCreate();
     // volley请求单例  
     MySingleton.getInstance().init(getApplicationContext());
     // 添加公共Header
     Map<String,String> headerMap =  new HashMap<>();
     // 添加Accept-Encoding: gzip
     headerMap.put("Accept-Encoding", "gzip");
     // 设置公共请求头
     MySingleton.getInstance().setHeaderMap(headerMap);
   }
}
```
## POST请求使用
```Java
String url = "http://www.example.com";

HashMap<String, String> params = new HashMap<>();
params.put("param1", param1Value);// 传入POST参数
// DataBaseJson 为自定义json返回对象
// reponseListener为com.android.volley.Response.Listener接口实现
Response.Listener<DataBaseJson> reponseListener = new Response.Listener<DataBaseJson>() {
    @Override
    public void onResponse(DataBaseJson response) {
      // TODO 请求成功处理
    }
};
// errorListener为com.android.volley.Response.ErrorListener接口实现
Response.ErrorListener errorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      // TODO 请求失败处理
    }
};
CustomRequest<DataBaseJson> jsObjRequest = new CustomRequest<>(
        Request.Method.POST, url, DataBaseJson.class, null, params,
        reponseListener, errorListener);
// 添加请求到队列
MySingleton.getInstance().getRequestQueue().add(jsObjRequest);
```
## multipart/form-data的POST方式HTTP请求(只支持简单的String参数)
```Java
String url = "http://www.example.com";

HashMap<String, String> params = new HashMap<>();
params.put("param1", param1Value);// 传入POST参数String
// DataBaseJson 为自定义json返回对象
// reponseListener为com.android.volley.Response.Listener接口实现
Response.Listener<DataBaseJson> reponseListener = new Response.Listener<DataBaseJson>() {
    @Override
    public void onResponse(DataBaseJson response) {
      // TODO 请求成功处理
    }
};
// errorListener为com.android.volley.Response.ErrorListener接口实现
Response.ErrorListener errorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      // TODO 请求失败处理
    }
};
try {

  MultipartRequest<DataBaseJson> jsObjRequest = new MultipartRequest<>(
          Request.Method.POST, url, DataBaseJson.class, null, params,
          reponseListener, errorListener);
  // 添加请求到队列
  MySingleton.getInstance().getRequestQueue().add(jsObjRequest);
} catch (UnsupportedEncodingException e){
  // 不支持编码异常
  e.printStackTrace();
}
```
## multipart/form-data的POST方式HTTP请求(支持File对象)
```Java
String url = "http://www.example.com";

HashMap<String, String> params = new HashMap<>();
params.put("param1", param1Value);// 传入POST参数File or String
// DataBaseJson 为自定义json返回对象
// reponseListener为com.android.volley.Response.Listener接口实现
Response.Listener<DataBaseJson> reponseListener = new Response.Listener<DataBaseJson>() {
    @Override
    public void onResponse(DataBaseJson response) {
      // TODO 请求成功处理
    }
};
// errorListener为com.android.volley.Response.ErrorListener接口实现
Response.ErrorListener errorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      // TODO 请求失败处理
    }
};
try {
  MultipartFileRequest<DataBaseJson> jsObjRequest = new MultipartFileRequest<>(
          Request.Method.POST, url, DataBaseJson.class, null, params,
          reponseListener, errorListener);
  // 添加请求到队列
  MySingleton.getInstance().getRequestQueue().add(jsObjRequest);
} catch (UnsupportedEncodingException e){
  // 不支持编码异常
  e.printStackTrace();
}
```
## GET请求使用
```Java
String url = "http://www.example.com?param1=" + "param1Value";// 传入GET参数
// DataBaseJson 为自定义json返回对象
// reponseListener为com.android.volley.Response.Listener接口实现
Response.Listener<DataBaseJson> reponseListener = new Response.Listener<DataBaseJson>() {
    @Override
    public void onResponse(DataBaseJson response) {
      // TODO 请求成功处理
    }
};
// errorListener为com.android.volley.Response.ErrorListener接口实现
Response.ErrorListener errorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      // TODO 请求失败处理
    }
};
CustomRequest<DataBaseJson> jsObjRequest = new CustomRequest<>(
        Request.Method.GET, url, DataBaseJson.class, null, null,
        reponseListener, errorListener);
// 添加请求到队列
MySingleton.getInstance().getRequestQueue().add(jsObjRequest);
```
# Gradle引入
`compile 'com.zyl.androidvolleyutils:androidvolleyutils:0.0.1'`
