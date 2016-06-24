package com.example.malik.meetingstest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CreationScreen extends AppCompatActivity {

    private Button backButton, createButton;
    private EditText accountEditable,subjectEditable,contactEditable,dateEditable,timeEditable;
    private TextView createScreenLabel;
    public String dailyIP = "http://192.168.1.40";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_creation_screen);

            createScreenLabel = (TextView)findViewById(R.id.createScreenLabel);
            createScreenLabel.setText(getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE3));
            accountEditable = (EditText)findViewById(R.id.accountEditable);
            subjectEditable = (EditText)findViewById(R.id.subjectEditable);
            contactEditable = (EditText)findViewById(R.id.contactEditable);
            dateEditable = (EditText)findViewById(R.id.dateEditable);
            timeEditable = (EditText)findViewById(R.id.timeEditable);

            backButton = (Button) findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goMain();
                }
            });

            createButton = (Button) findViewById(R.id.createButton);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncRestRequest created = new AsyncRestRequest();
                    created.execute();
                    finish();
                }
            });
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private void goMain() {
        finish();
    }

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String module = createScreenLabel.getText().toString();
        String name = accountEditable.getText().toString();

        @Override
        protected String doInBackground(String... params) {

            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPOST = new
                        HttpPost(dailyIP+"/suiteRest/Api/V8/module/" + module +"/");
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",name);


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPOST.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPOST.setHeader("Accept", "application/json");
                httpPOST.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPOST);
                // 9. receive response as inputStream
                //                  inputStream = httpResponse.getEntity().getContent();
                //                  // 10. convert inputstream to string
                //                  if(inputStream != null)
                //                      result = convertInputStreamToString(inputStream);
                //                  else
                //                      result = "Did not work!";
            }
            catch (Exception e){
                System.out.println(e);
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
