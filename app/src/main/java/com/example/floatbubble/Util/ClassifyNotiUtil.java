package com.example.floatbubble.Util;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.floatbubble.MainActivity;
import com.example.floatbubble.data.LabelFlags;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassifyNotiUtil {
    //TODO 状态量
    //
    private static int label = LabelFlags.REMINDER;
    private static String url = "http://106.13.128.118:5001/demo";

    //处理方法
    public static int analyseNoti(String text) {
        //判断逻辑
        label = LabelFlags.REMINDER;//默认标签
        new Thread(() -> {
            //发送数据,获取标签
            LogUtil.d("通知", "autoTagLabel: " + text);
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("demo", text);
            Request request = new Request.Builder().url(url).post(builder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i("连接","服务器错误");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        final String res = response.body().string();
                        Log.d("回应", "autoTagLabel: " + res);
                        int temp = Integer.parseInt(res);
                         if (temp >= LabelFlags.NEWS && temp <= LabelFlags.OTHERS) {
                            label = temp;
                         } else {
                             label = LabelFlags.REMINDER;
                         }
                    }
                }
            });
        }).start();
        return label;
    }

}
