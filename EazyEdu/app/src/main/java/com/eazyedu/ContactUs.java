package com.eazyedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import 	android.app.Fragment;
import android.app.Activity;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


public class ContactUs extends Activity{

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

                /**
                 * Validation check
                 */
                 if (name.trim().length()==0
                         || email.trim().length()==0
                         || question.trim().length()==0){
                     /**
                      *  Some fields are empty. Request to fill in all fields
                      */
                     Toast.makeText(ContactUs.this,"Please fill up all the fields!",Toast.LENGTH_LONG).show();
                 } else {
                     /**
                      *  The fields are populated. Send the Email message
                      */

                     name_text.getText().clear();
                     email_text.getText().clear();
                     question_text.getText().clear();

                     Toast.makeText(ContactUs.this, "Your message has been submitted", Toast.LENGTH_LONG).show();
                /*Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mkyong.com"));
                startActivity(browserIntent); */
                 }
            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
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
            View rootView = inflater.inflate(R.layout.activity_contact_us,container, false);
            return rootView;
        }
    }

}
