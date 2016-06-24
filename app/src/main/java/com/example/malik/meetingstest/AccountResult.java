package com.example.malik.meetingstest;


import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;


public class AccountResult extends AppCompatActivity {

    private Button backButton,updateButton, editButton;
    private ImageButton gMapsButton, playAudio;
    private TextView infoViewer, titleViewer, headerView, moduleView, headerTitle, bonusView, textView2, contactTitle;
    private String words,body = "", moduleTitle,parentID = "", dailyIP = "http://192.168.1.40";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_result);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        words = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE);
        final String bits [] = words.split("\n");

        final TextToSpeech ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        ttobj.setLanguage(Locale.UK);


        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerView.setEnabled(true);
                if (moduleTitle.equals("Accounts")){
                    infoViewer.setEnabled(false);
                }
                else if(moduleTitle.equals("Contacts")){
                    infoViewer.setEnabled(false);
                }
                else
                infoViewer.setEnabled(true);
                updateButton.setClickable(true);
                editButton.setClickable(false);
            }
        });

        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerView.setEnabled(false);
                infoViewer.setEnabled(false);
                updateButton.setClickable(false);
                editButton.setClickable(true);
                UpdateDataRequest update = new UpdateDataRequest();
                update.execute();

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

        headerView = (EditText)findViewById(R.id.headerView);
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

        infoViewer = (EditText)findViewById(R.id.infoViewer);

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

        playAudio = (ImageButton) findViewById(R.id.playAudio);
        playAudio.setOnClickListener(new View.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick (View v){
                ttobj.speak(body,TextToSpeech.QUEUE_FLUSH,null,"utteranceId");
            }
        });

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
    private void refresh(){
        finish();
        startActivity(getIntent());
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
                URL url = new URL(dailyIP+"/suiteRest/Api/V8/module/Accounts/"+params[0]+"?XDEBUG_SESSION_START=PHPSTORM");
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

    private class UpdateDataRequest extends AsyncTask<String,String,String> {

        String idNum = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE2);
        String module = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE1);
        String updateField = infoViewer.getText().toString();
        String upateField1 = headerView.getText().toString();
        int responseCode;

        @Override
        protected String doInBackground(String... params) {
            try {

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPut httpPUT = new
                        HttpPut(dailyIP+"/suiteRest/Api/V8/module/" + module +"/" + idNum);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                if (module.equals("Meetings")) {
                    jsonObject.put("description", updateField);
                    jsonObject.put("name", upateField1);
                }
                else if (module.equals("Accounts")){
                    jsonObject.put ("website",upateField1);
                }
                else if (module.equals("Contacts")){
                    jsonObject.put("email",upateField1);
                }


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPUT.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPUT.setHeader("Accept", "application/json");
                httpPUT.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPUT);
                // 9. receive response as inputStream
                //                  inputStream = httpResponse.getEntity().getContent();
                //                  // 10. convert inputstream to string
                //                  if(inputStream != null)
                //                      result = convertInputStreamToString(inputStream);
                //                  else
                //                      result = "Did not work!";

                   }

            catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


}
