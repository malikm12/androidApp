package com.example.malik.meetingstest;


import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;


public class AccountResult extends AppCompatActivity {

    private Button backButton;
    private ImageButton gMapsButton;
    private TextView infoViewer, titleViewer, headerView, moduleView, headerTitle, bonusView, textView2, contactTitle;
    private String words,body = "", moduleTitle,parentID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_result);

        words = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE);
        final String bits [] = words.split("\n");

        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });

        moduleTitle = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE1);

        moduleView = (TextView)findViewById(R.id.moduleView);

        if(moduleTitle.equals("Accounts")){
            moduleView.setText("Account Name:");
        }
        else if (moduleTitle.equals("Contacts")){
            moduleView.setText("Contact Name:");
        }
        else if (moduleTitle.equals("Meetings")){
            moduleView.setText("Meeting:");
        }


        titleViewer = (TextView)findViewById(R.id.titleViewer);
        titleViewer.setText(bits[0]);

        headerTitle = (TextView)findViewById(R.id.headerTitle);
        if (moduleView.getText().equals("Account Name:")){
            headerTitle.setText("Website:");
        }
        else if (moduleView.getText().equals("Contact Name:")){
            headerTitle.setText("Email:");
        }
        else if (moduleView.getText().equals("Meeting:")){
            headerTitle.setText("Subject:");
        }

        headerView = (TextView)findViewById(R.id.headerView);
        headerView.setText(bits[1]);

        contactTitle = (TextView)findViewById(R.id.contactTitle);
        if (headerTitle.getText().equals("Subject:")){
            contactTitle.setText("Contact:");
        }

        bonusView = (TextView)findViewById(R.id.bonusView);
        if(headerTitle.getText().equals("Subject:")){
            bonusView.setText(bits[2]);
        }

        textView2 = (TextView) findViewById(R.id.textView2);
        if (moduleTitle.equals("Accounts")){
            textView2.setText("Address:");
        }
        if (moduleTitle.equals("Contacts")){
            textView2.setText("Address:");
        }
        if (moduleTitle.equals("Meetings")){
            textView2.setText("Notes:");
        }

        infoViewer = (TextView)findViewById(R.id.infoViewer);

        if(textView2.getText().equals("Address:")){
            for (int i = 2; i< bits.length; i++){
                body += bits[i] + "\n";
            }
        }
        else if(textView2.getText().equals("Notes:")){
            for (int i = 3; i< bits.length-1; i++){
                body += bits[i] + "\n";
            }
        }

        infoViewer.setText(body);

        parentID = bits[bits.length-1];

        gMapsButton = (ImageButton) findViewById(R.id.gMapsButton);
        gMapsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                NewDataRequest mapLocation = new NewDataRequest();
                mapLocation.execute(parentID);
            }
        });


    }
    private void goback() {
        finish();
    }

    private void onMap(String location){

        Uri mapsIntentUri = Uri.parse("geo:0,0?q=".concat(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
        startActivity(mapIntent);
    }



    private class NewDataRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONObject fields = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.1.38/suiteRest/Api/V8/module/Accounts/"+params[0]);
                HttpURLConnection suiteConnection = (HttpURLConnection) url.openConnection();
                suiteConnection.setRequestMethod("GET");

                if (suiteConnection.getRequestMethod().equals("GET")){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suiteConnection.getInputStream()));

                    String line;
                    // read from the urlconnection via the bufferedreader
                    while ((line = bufferedReader.readLine()) != null) {

                        reader += (line + "\n");
                        // System.out.println(line);
                    }

                    bufferedReader.close();

                    JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("billing_address_street") + "\n";
                            content += fields.getString("billing_address_city") + "\n";
                            content += fields.getString("billing_address_state") + "\n";
                            content += fields.getString("billing_address_postalcode") + "\n";
                            content += fields.getString("billing_address_country") + "\n";
                        }

                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            return params[0];
        }


        @Override
        protected void onPostExecute(String result) {
            onMap(content);
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


}
