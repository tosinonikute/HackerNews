// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.hackernewsapp.discussion;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hackernewsapp.R;
import com.hackernewsapp.util.Misc;
import com.hackernewsapp.util.ui.MaterialProgressBar;

/**
 * This Activity is used as a fallback when there is no browser installed that supports
 * Chrome Custom Tabs
 */
public class WebviewActivity extends AppCompatActivity {

    public String EXTRA_URL = "";
    private String url;
    private MaterialProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String url = getIntent().getStringExtra(EXTRA_URL);
        progressBar = (MaterialProgressBar) findViewById(R.id.material_progress_bar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("EXTRA_URL");
        }

        if(url != null) {
            loadWebView(url);
        }

    }

    public void loadWebView(String url){

        progressBar.setVisibility(View.VISIBLE);
        setTitle(url);
        final WebView webview= (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Misc.displayLongToast(getApplicationContext(), description);
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);
            }
        });
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(url);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
