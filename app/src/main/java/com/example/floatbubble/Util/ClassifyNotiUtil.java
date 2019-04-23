package com.example.floatbubble.Util;

import com.example.floatbubble.data.LabelFlags;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassifyNotiUtil {
    //TODO 状态量
    //
    private static int label = LabelFlags.REMINDER;//默认标签
    private static String url = "http://106.13.128.118:5001/demo";

    //处理方法
    public static int analyseNoti(String text) {
        //判断逻辑
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
                    LogUtil.d("通知连接","服务器错误");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        final String res = response.body().string();
                        LogUtil.d("通知回应", "autoTagLabel: " + res);
                        int temp = Integer.parseInt(res);
                         if (temp >= LabelFlags.NEWS && temp <= LabelFlags.OTHERS) {
                            label = temp;
                         }
                    }
                }
            });
        }).start();
        LogUtil.d("通知","未判断");
        return label;
    }

}
