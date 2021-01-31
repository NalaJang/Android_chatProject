package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private WebView webView;
    final String urlStr = "http://192.168.0.17:8080/webapp/webServer/daum.html";

    class MyJavaScriptInterface {

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processData(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);   //자바 스크립트 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);

//        textView_address  = findViewById(R.id.textView_address);

//        init_webView();



        //자바 스크립트 이벤트에 대응할 함수를 정의 한 클래스를 붙여준다.
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {


                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

//        webView.setWebChromeClient(new WebChromeClient());
        //주소 변경하기
        webView.loadUrl("http://192.168.0.17:8080/webapp/webServer/daum.html");



//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
////                userPost(urlStr);
//
//            }
//        }).start();



    }   //end onCreate
/*
    public void init_webView() {

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
        webView.loadUrl(urlStr);
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView_address.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });
        }
    }
/* ->                               찾아보기 !!

 */
    /*
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        Uri uri = Uri.parse(view.getUrl()); //url을 uri로 변경

        if (uri.getPath().contains("/test/page")) {  // 현재 uri의 path에 컨테인만 읽기

            Intent intent = new Intent(MainActivity.this, TestActivity.class); // 새창을 여는 액티비티나, 팝업일때 이용하면 용이합니다.
            intent.putExtra("url", view.getUrl());
            startActivity(intent);

            return true;
        }

//        return super.shouldOverrideUrlLoading(view, request);

    }

     */


    public void userPost(String urlStr) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=1234&pw=1234";
                outputStream.write(params.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                while(true) {
                    line = reader.readLine();

                    if(line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }

                reader.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUserPost(output.toString());
        Log.d("===========post=============", output.toString());
    }

    public void setUserPost(String str) {
        Document doc = Jsoup.parse(str);
        Elements address1 = doc.select("input.address1");

//        Log.d("==============address1==========", address1.get(0).text());

    }
}