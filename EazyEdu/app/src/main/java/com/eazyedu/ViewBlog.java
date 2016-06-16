package com.eazyedu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class ViewBlog extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewBlogPane();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void viewBlogPane(){

         final WebView blog_view;

        blog_view = (WebView) findViewById(R.id.webView_blog);
        blog_view.setInitialScale(80);
        blog_view.setWebViewClient(new WebViewClient());
        blog_view.getSettings().setJavaScriptEnabled(true);
        WebSettings mWebSettings = blog_view.getSettings();
        mWebSettings.setBuiltInZoomControls(true);
        blog_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        blog_view.setScrollbarFadingEnabled(false);
        blog_view .getSettings().setDomStorageEnabled(true);
        blog_view.loadUrl("http://eazyedu.weebly.com/blog---learn-more-info");

        //trying to remove the header from the WebView
        blog_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                blog_view.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByTagName('header')[0];"
                        + "head.parentNode.removeChild(head);" +
                        "})()");
            }
        });

    }

}
