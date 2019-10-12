package com.eazyedu.blog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
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
        SpannableStringBuilder blogContentStrBuilder=new SpannableStringBuilder();

        try {
            viewBlogDocument = Jsoup.connect(blogWebURL).get();
        } catch (IOException e) {
            int d = Log.d("viewBlogPane: ", "FATAL! IOEXCEPTION detected " + e);
            blogContentStrBuilder.append("Cannot connect to Source!");
            setTextInTxtView(blogContentStrBuilder);
            return txtView;
        }


        Elements allBlogElements = viewBlogDocument.getElementsByClass("blog-post");
        Iterator<Element> elementIterator = allBlogElements.iterator();
        Integer blogContentCounter = new Integer(0);
        while(elementIterator.hasNext()){

            Element blogElement = elementIterator.next();
            Elements blogTitleElements = blogElement.select("h2");
            if(!blogTitleElements.isEmpty()) {
                blogContentCounter++;
                String headingTitle = blogTitleElements.first().text().trim();
                SpannableString headingTxtSpannable = new SpannableString(blogContentCounter+". "+ headingTitle);
                StyleSpan headingBoldSpan = new StyleSpan(Typeface.BOLD);
                headingTxtSpannable.setSpan(headingBoldSpan,0,headingTxtSpannable.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                blogContentStrBuilder.append(headingTxtSpannable);
                blogContentStrBuilder.append("\n");

                Elements blogTitleContentElements = blogElement.getElementsByClass("paragraph");
                if(!blogTitleContentElements.isEmpty()){
                    String blogContent = blogTitleContentElements.first().text().trim();
                    SpannableString blogContntSpannable = new SpannableString(blogContent);
                    blogContentStrBuilder.append(blogContntSpannable);
                    blogContentStrBuilder.append("\n");
                    blogContentStrBuilder.append("\n");
                }
            }
        }
        setTextInTxtView(blogContentStrBuilder);
        return txtView;
    }

    private void setTextInTxtView(SpannableStringBuilder builder){

        txtView.setText(builder, TextView.BufferType.SPANNABLE);
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