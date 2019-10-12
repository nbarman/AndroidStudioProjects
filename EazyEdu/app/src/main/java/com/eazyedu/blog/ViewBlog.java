package com.eazyedu.blog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eazyedu.R;
import org.jsoup.nodes.Document;
import java.util.concurrent.ExecutionException;

public class ViewBlog extends AppCompatActivity {
    private Document viewBlogDocument;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public TextView txtView;
    private ProgressBar blogProgressBar;
    private final static String blogWebURL = "https://eazyedu.weebly.com/blog---learn-more-info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewBlogPane();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void viewBlogPane(){
        final WebView blog_view;
        blogProgressBar = (ProgressBar) findViewById(R.id.blogProgressBar);

        txtView = (TextView) findViewById(R.id.txtView_blog);
        BlogConnectTask contentFromURLTsk = new BlogConnectTask(txtView);
        String[] URLs = {ViewBlog.blogWebURL};
        blogProgressBar.setVisibility(View.INVISIBLE);
        try {
            txtView = contentFromURLTsk.execute(URLs).get();

        } catch (InterruptedException | ExecutionException exception){
            Log.e("FATAL!", exception.getMessage());
        }
    }

    private void showTextView(){
        if(txtView!=null && blogProgressBar!=null){

            txtView.setVisibility(View.VISIBLE);
            blogProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showProgressBar(){
        if(txtView!=null && blogProgressBar!=null){

            txtView.setVisibility(View.INVISIBLE);
            blogProgressBar.setVisibility(View.VISIBLE);
        }
    }
}