package com.example.anonymous.campussocialportal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class pdfview extends AppCompatActivity {
    private WebView wv1;
    String url;
    String q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");


        wv1=(WebView)findViewById(R.id.WebView);
        wv1.setWebViewClient(new MyBrowser());





        //String f = URLEncoder.encode("https://firebasestorage.googleapis.com/v0/b/uploadpdf-813bd.appspot.com/o/uploads%2F1535647411202.pdf?alt=media&token=bdf11c2a-8221-4ffc-9420-794193804f39","UTF-8");
        // myWebView.loadUrl();


        //String url = "http://drive.google.com/viewerng/viewer?embedded=true&url=https://firebasestorage.googleapis.com/v0/b/realtime-chat-46f4c.appspot.com/o/documents%2Fbf307aa5-79ae-4532-8128-ee394537b357.pdf?alt=media&token=2d0c5329-4717-4adc-9418-6614913e5bfa";
        try {
            if(id.equals("attendance")){
q="https://firebasestorage.googleapis.com/v0/b/pietportal-f50ce.appspot.com/o/uploads%2F1535678998259.pdf?alt=media&token=6bd3e0a6-821d-4179-9567-cdee74344b4d";
            }
            else if(id.equals("timetable")){
                q ="https://firebasestorage.googleapis.com/v0/b/pietportal-f50ce.appspot.com/o/uploads%2F1535679053454.pdf?alt=media&token=3d092a15-5a96-4db2-9c35-675391571a42";

            }
            else if(id.equals("sessionals")){
                q="https://firebasestorage.googleapis.com/v0/b/pietportal-f50ce.appspot.com/o/uploads%2F1535678963755.pdf?alt=media&token=ea4f3910-ce54-47d5-b458-04c74001db9f";

            }
            else if(id.equals("result")){
                q="https://firebasestorage.googleapis.com/v0/b/pietportal-f50ce.appspot.com/o/uploads%2F1535678963755.pdf?alt=media&token=ea4f3910-ce54-47d5-b458-04c74001db9f";

            }
           //  q = "https://firebasestorage.googleapis.com/v0/b/uploadpdf-813bd.appspot.com/o/uploads%2F1535647411202.pdf?alt=media&token=bdf11c2a-8221-4ffc-9420-794193804f39";
            url = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + URLEncoder.encode(q, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setDisplayZoomControls(false);
        wv1.getSettings().setBuiltInZoomControls(true);
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