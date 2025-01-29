package com.example.anonymous.admin;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class webview extends AppCompatActivity {
    private WebView wv1;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        wv1=(WebView)findViewById(R.id.WebView);

        wv1.setWebViewClient(new MyBrowser());
        Intent intent = getIntent();
        String weburl= intent.getStringExtra("url");




        //String f = URLEncoder.encode("https://firebasestorage.googleapis.com/v0/b/uploadpdf-813bd.appspot.com/o/uploads%2F1535647411202.pdf?alt=media&token=bdf11c2a-8221-4ffc-9420-794193804f39","UTF-8");
        // myWebView.loadUrl();


        //String url = "http://drive.google.com/viewerng/viewer?embedded=true&url=https://firebasestorage.googleapis.com/v0/b/realtime-chat-46f4c.appspot.com/o/documents%2Fbf307aa5-79ae-4532-8128-ee394537b357.pdf?alt=media&token=2d0c5329-4717-4adc-9418-6614913e5bfa";
        try {
            String q = "https://firebasestorage.googleapis.com/v0/b/uploadpdf-813bd.appspot.com/o/uploads%2F1535647411202.pdf?alt=media&token=bdf11c2a-8221-4ffc-9420-794193804f39";
            url = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + URLEncoder.encode(weburl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setDisplayZoomControls(false);
        wv1.getSettings().setBuiltInZoomControls(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl(url);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}