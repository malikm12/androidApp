package com.example.malik.meetingstest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText editable1,editable1_5,editable2,editable3,editable4,editable5;
    private TextView createScreenLabel,field1,field1_5, field2, field3, field4, field5;
    public String dailyIP = "http://192.168.1.40";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_creation_screen);

            createScreenLabel = (TextView)findViewById(R.id.createScreenLabel);
            createScreenLabel.setText(getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE3));

            field1 = (TextView) findViewById(R.id.field1);
            field1_5 = (TextView) findViewById(R.id.field1_5);
            field2 = (TextView) findViewById(R.id.field2);
            field3 = (TextView) findViewById(R.id.field3);
            field4 = (TextView) findViewById(R.id.field4);
            field5 = (TextView) findViewById(R.id.field5);
            editable1 = (EditText)findViewById(R.id.editable1);
            editable1_5=(EditText) findViewById(R.id.editable1_5);
            editable2 = (EditText)findViewById(R.id.editable2);
            editable3 = (EditText)findViewById(R.id.editable3);
            editable4 = (EditText)findViewById(R.id.editable4);
            editable5 = (EditText)findViewById(R.id.editable5);

            if (createScreenLabel.getText().toString().equals("Accounts")){
                field1.setText("Name");
                field2.setText("Phone");
                field3.setText("Website");
                field4.setText("Industry");
                field5.setText("Country");
                editable1_5.setEnabled(false);

            }

            if (createScreenLabel.getText().toString().equals("Contacts")) {
                field1.setText("First Name");
                field1_5.setText("Surname");
                field2.setText("Account");
                field3.setText("Title");
                field4.setText("Phone");
                field5.setText("");
                editable5.setEnabled(false);
            }

            if (createScreenLabel.getText().toString().equals("Meetings")) {
                field1.setText("Account");
                field2.setText("Date");
                field3.setText("Time");
                field4.setText("Contact");
                field5.setText("Location");
                editable1_5.setEnabled(false);
            }


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

    //private String [] splitter(String name){
    //     String nameArray [] = name.split(" ");
    //
    //      return nameArray;
    //}

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String module = createScreenLabel.getText().toString();
        String firstName = editable1.getText().toString();
        String surname = editable1_5.getText().toString();
        String info2 = editable2.getText().toString();
        String info3 = editable3.getText().toString();
        String info4 = editable4.getText().toString();
        String info5 = editable5.getText().toString();
        int responseCode;

        @Override
        protected String doInBackground(String... params) {

            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPOST = new
                        HttpPost(dailyIP+"/suiteRest/Api/V8/module/" + module);
                String json;
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                if(module.equals("Accounts")){
                    jsonObject.put("name",firstName);
                    jsonObject.put ("phone_office",info2);
                    jsonObject.put("website",info3);
                    jsonObject.put("industry", info4);
                    jsonObject.put("billing_address_country",info5);
                }

                if(module.equals("Meetings")){
                    jsonObject.put("parent_name",firstName);
                    jsonObject.put ("description",info2);
                }
                if (module.equals("Contacts")) {
                    jsonObject.put("first_name", firstName);
                    jsonObject.put("last_name", surname);
                    jsonObject.put("account_name", info2);
                    jsonObject.put("title", info3);
                    jsonObject.put("phone_work", info4);
                }




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
                System.out.println(httpResponse);
                responseCode = httpResponse.getStatusLine().getStatusCode();

            }
            catch (Exception e){
                System.out.println(e);
            }

            String respCode = Integer.toString(responseCode);

            return respCode;
        }


        @Override
        protected void onPostExecute(String responseCode) {

            if (responseCode.equals("200")) {
                Context context = getApplicationContext();
                CharSequence text = "Succesful creation!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = "Failed to create, please try again!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
}
