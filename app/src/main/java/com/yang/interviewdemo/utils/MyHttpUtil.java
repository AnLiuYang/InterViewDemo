package com.yang.interviewdemo.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * Created by Administrator on 2016/9/1.
 */
public class MyHttpUtil {

    public static void getHt(String url, final setNotifyResultListener notifyResultListener){
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (notifyResultListener != null)
                    notifyResultListener.postData(result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (notifyResultListener != null)
                    notifyResultListener.postData("");
            }
        });
    }

    public static void postHttp(String url , RequestParams params, final setNotifyResultListener notifyResultListener){
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (notifyResultListener != null)
                    notifyResultListener.postData(result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (notifyResultListener != null)
                    notifyResultListener.postData("");
            }
        });
    }

    public static interface setNotifyResultListener{
        public void postData(String result);
    }
}
