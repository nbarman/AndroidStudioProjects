package com.eazyedu.blog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author nambarma
 * This is a async task to connect to the blog url and parse and set the textview using JSoup
 */

public class BlogConnectTask extends AsyncTask<String, Void, TextView> {
    private Document viewBlogDocument;
    private TextView txtView;
    private SpannableStringBuilder spanStr;

    public BlogConnectTask(TextView txtView){
        this.txtView = txtView;
        configureTxtView();
        spanStr = new SpannableStringBuilder();
    }
    @Override
    protected TextView doInBackground(String... strings) {

        String blogWebURL = Arrays.asList(strings).get(0);

        try {
            viewBlogDocument = Jsoup.connect(blogWebURL).get();
        } catch (IOException e) {
            int d = Log.d("viewBlogPane: ", "FATAL! IOEXCEPTION detected " + e);
            txtView.setText("Cannot connect to Source");
            return txtView;
        }


        Elements allBlogElements = viewBlogDocument.getElementsByClass("blog-post");
        Iterator<Element> elementIterator = allBlogElements.iterator();

        while(elementIterator.hasNext()){

        Element blogElement = elementIterator.next();
        Elements blogTitleElements = blogElement.select("h2");
        if(!blogTitleElements.isEmpty()) {
            String headingTitle = blogTitleElements.first().text().trim();
            txtView.append("\n");
            txtView.append(Html.fromHtml("<b><br><font color=#2bb1ff>"+ headingTitle +"</font><br></b>"));
            txtView.append("\n");

            Elements blogTitleContentElements = blogElement.getElementsByClass("paragraph");
            if(!blogTitleContentElements.isEmpty()){
                txtView.append(blogTitleContentElements.first().text().trim() + "\n");
            }
        }
        txtView.append("\n\n");
    }
        return txtView;
    }

    private void setHeadingSpan(SpannableStringBuilder spanStr){
        spanStr.setSpan(new StyleSpan(Typeface.BOLD),spanStr.length(),spanStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    private void clearHeadingSpan(SpannableStringBuilder spanStr){
        spanStr.clearSpans();
    }

    private void resetTextView(){
        txtView.setAllCaps(false);
        txtView.setTextColor(Color.BLACK);

    }

    private void configureTxtView(){
        txtView.setVerticalScrollBarEnabled(true);
        txtView.setMovementMethod(new ScrollingMovementMethod());
    }
}
