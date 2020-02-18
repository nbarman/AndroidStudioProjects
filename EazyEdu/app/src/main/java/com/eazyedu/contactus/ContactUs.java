package com.eazyedu.contactus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import 	android.app.Fragment;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.eazyedu.eazymail.EazyMail;
import com.eazyedu.R;

import android.os.AsyncTask;
import java.lang.StringBuilder;
import android.view.Gravity;

/**
 * This class handles all the contact request forms
 * NOTE: Email does not work with proxy servers
 * EazyEdu All Rights Reserved
 *
 * Developer : Namit Mohan Barman
 *
 */

public class ContactUs extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        /*View backgroundImage = findViewById(R.id.);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(80); */
        setContentView(R.layout.content_contact_us);
        addListenerOnButton();

    }


    public void addListenerOnButton() {



        Button button = (Button) findViewById(R.id.submit_button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText name_text = (EditText) findViewById(R.id.name_Text);
                EditText email_text = (EditText) findViewById(R.id.email_Text);
                EditText question_text = (EditText) findViewById(R.id.message_Text);

                String name = name_text.getText().toString();
                String email = email_text.getText().toString();
                String question = question_text.getText().toString();

                StringBuilder email_body = new StringBuilder();
                email_body.append("Hi, This is an inquiry from a prospective student\n\n");
                email_body.append("Name : "+ name+ "\n\n");
                email_body.append("Email : "+ email+ "\n\n");
                email_body.append("Question : "+ question+ "\n\n");

                /**
                 * Validation check
                 */
                if (name.trim().length() == 0
                        || email.trim().length() == 0
                        || question.trim().length() == 0) {
                    /**
                     *  Some fields are empty. Request to fill in all fields
                     */
                    Toast.makeText(ContactUs.this, "Please fill up all the fields!", Toast.LENGTH_LONG).show();
                } else {
                    /**
                     *  The fields are populated. Send the Email message
                     */

                    final EazyMail mail = new EazyMail("eazyedu.helpline@gmail.com", "NmbMyMan4");

                    // Setting the attributes of the mail

                    String[] toArr = {"eazyedu.helpline@gmail.com"};
                    mail.set_to(toArr);
                    mail.set_from("eazyedu.helpline@gmail.com");
                    mail.set_subject("EazyEdu Inquiry");
                    mail.set_body(email_body.toString());

                    //Sending the mail asynchronously
                    new AsyncTask<Void, Void, Void>() {
                        boolean send_mail_status= false;
                        @Override public Void doInBackground(Void... arg) {
                            try{
                                send_mail_status = mail.send();
                            }catch(Exception e) {
                            }
                        return null;
                        }

                        protected void onProgressUpdate() {
                            //called when the background task makes any progress
                            Toast.makeText(ContactUs.this, "Your message is being sent", Toast.LENGTH_LONG).show();
                        }

                        protected void onPreExecute() {
                            //called before doInBackground() is started
                        }
                        protected void onPostExecute() {
                            //called after doInBackground() has finished
                        }
                    }.execute();

                    // Notify the user that the message has been send / not send



                    if(true) {
                        Toast confirm_msg = Toast.makeText(ContactUs.this, "Your message has been submitted! We will get back to you soon!", Toast.LENGTH_LONG);
                        confirm_msg.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        confirm_msg.show();
                    } else {
                        Toast.makeText(ContactUs.this, "Email was not send", Toast.LENGTH_LONG).show();
                    }

                    // Clear the textfields
                    name_text.getText().clear();
                    email_text.getText().clear();
                    question_text.getText().clear();
                /*Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mkyong.com"));
                startActivity(browserIntent); */
                }
            }

        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_contact_us, container, false);
            return rootView;
        }
    }

}
