package com.example.temperature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private WebView wv_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv_webview = findViewById(R.id.wv_webview);
        //访问网页
        wv_webview.loadUrl("http://x.nlecloud.com/my/login?returnUrl=/project/47142/app");
//系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        wv_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//使用WebView加载显示url
                view.loadUrl(url);
                return true;
            }
        });
    }


    private void requestData() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final User user = new User();
        user.setAccount("15957871471");
        user.setPassword("123456");
        Gson gson = new Gson();
        String Json = gson.toJson(user);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url("http://api.nlecloud.com/users/login")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                Log.e("返回", msg.obj.toString());
            }
            return true;
        }
    });
}
