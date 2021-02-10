package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;

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

        //웹뷰 세팅(새창과 관련한 동작)
        webView.getSettings().setJavaScriptEnabled(true);   //자바 스크립트 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);   //팝업창 띄우기
        webView.getSettings().setSupportMultipleWindows(true);  //여러개의 윈도우 사용


        //자바 스크립트 브릿지(자바 스크립트 이벤트에 대응할 함수를 정의 한 클래스를 붙여준다.)
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {


                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        //주소 변경하기
        webView.loadUrl("http://192.168.0.17:8080/webapp/webServer/daum.html");



    }   //end onCreate
}