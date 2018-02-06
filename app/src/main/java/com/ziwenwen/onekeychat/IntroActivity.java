package com.ziwenwen.onekeychat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ziwenwen.onekeychat.view.CustomWebView;

/**
 * Created by ziwen.wen on 2018/2/5.
 */
public class IntroActivity extends AppCompatActivity {

    CustomWebView webView;
    View loadingView;
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            loadingView.setVisibility(View.GONE);
            injectCSS();
            super.onPageFinished(view, url);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("使用说明");

        setContentView(R.layout.activitty_intro);
        webView = findViewById(R.id.custom_web_view);
        loadingView = findViewById(R.id.progress_bar);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl("https://note.youdao.com/share/mobile.html?id=9ffb958bf8cd50318b71bc48282d0cfe&type=note#/");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Inject CSS method: read style.css from assets folder
// Append stylesheet to document head
    private void injectCSS() {
        try {
            String encoded = Base64.encodeToString(CUSTOM_CSS.getBytes(), Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String CUSTOM_CSS = "#text-sdk," +
            "#img-sdk," +
            "#app-dl," +
            ".footer-wrap," +
            ".banner {" +
            "display: none;" +
            "}";


}
