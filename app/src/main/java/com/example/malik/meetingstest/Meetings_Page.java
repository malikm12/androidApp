package com.example.malik.meetingstest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Meetings_Page extends AppCompatActivity {


    private Button backButton;
    private ListView list;
    private TextView dataTitle;
    private String module;
    public final static String EXTRA_MESSAGE = "com.example.malik.meetingstest.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.malik.meetingstest.MESSAGE1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings__page);


        dataTitle = (TextView) findViewById(R.id.dataTitle);
        dataTitle.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE1));

        module = (String) dataTitle.getText();

        final ArrayList<String> values = new ArrayList<String>();

        list = (ListView) findViewById(R.id.listView);
        String data = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);

        final ArrayList<String> ids = new ArrayList<String>();

        if(dataTitle.getText().equals("Accounts")) {
            try {
                JSONArray dataArray = new JSONArray(data);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    values.add(obj.getString("name"));

                    ids.add(obj.getString("id"));

                }

            } catch (Exception err) {
                System.out.println(err.getMessage());
            }
        }
        else if (dataTitle.getText().equals("Contacts")){
            try {
                JSONArray dataArray = new JSONArray(data);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    values.add(obj.getString("name"));

                    ids.add(obj.getString("id"));

                }

            } catch (Exception err) {
                System.out.println(err.getMessage());
            }
        }
        else if (dataTitle.getText().equals("Meetings")){
            try {
                JSONArray dataArray = new JSONArray(data);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    values.add(obj.getString("parent_name"));

                    ids.add(obj.getString("id"));

                }

            } catch (Exception err) {
                System.out.println(err.getMessage());
            }
        }
        else if (dataTitle.getText().equals("Opportunities")){
            try {
                JSONArray dataArray = new JSONArray(data);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    values.add(obj.getString("name"));

                    ids.add(obj.getString("id"));

                }

            } catch (Exception err) {
                System.out.println(err.getMessage());
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        // ListView Item Click Listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String accountId = "";
                try {

                    accountId = ids.get(position);
                }
                catch(Exception err)
                {
                    System.out.println(err.getMessage());
                }


               hitResult(accountId,module);

            }

        });


        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
            }
        });

    }

    private void goMain() {
        finish();
    }

    private void displayResult(String content, String module){
        Intent gotoResult = new Intent(this, AccountResult.class);
        gotoResult.putExtra(EXTRA_MESSAGE, content);
        gotoResult.putExtra(EXTRA_MESSAGE1,module);
        startActivity(gotoResult);
    }

    private void hitResult(String accountID,String module) {
        AsyncRestRequest singularCall = new AsyncRestRequest();
        singularCall.execute(accountID);

    }

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONObject fields = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.1.38/suiteRest/Api/V8/module/"+ module +"/"+params[0]);
                HttpURLConnection suiteConnection = (HttpURLConnection) url.openConnection();
                suiteConnection.setRequestMethod("GET");

                if (suiteConnection.getRequestMethod().equals("GET")){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suiteConnection.getInputStream()));

                    String line;
                    // read from the urlconnection via the bufferedreader
                    while ((line = bufferedReader.readLine()) != null) {

                        reader += (line + "\n");
                    }

                    bufferedReader.close();

                    if (module.equals("Accounts")){

                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields != null) {
                            content += fields.getString("name") + "\n";
                            content += fields.get("website") + "\n";
                            content += fields.getString("billing_address_street") + "\n";
                            content += fields.getString("billing_address_city") + "\n";
                            content += fields.getString("billing_address_state") + "\n";
                            content += fields.getString("billing_address_postalcode") + "\n";
                            content += fields.getString("billing_address_country") + "\n";
                        }
                    }

                    else if (module.equals("Contacts")){
                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("name") + "\n";
                            content += fields.getString("email1") + "\n";
                            content += fields.getString("primary_address_street") + "\n";
                            content += fields.getString("primary_address_city") + "\n";
                            content += fields.getString("primary_address_state") + "\n";
                            content += fields.getString("primary_address_postalcode") + "\n";
                            content += fields.getString("primary_address_country") + "\n";
                        }
                    }

                    else if (module.equals("Meetings")){
                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("parent_name") + "\n";
                            content += fields.getString("name") + "\n";
                            content += fields.getString("assigned_user_name") + "\n";
                            content += fields.getString("description") + "\n";
                            content += fields.getString("parent_id") + "\n";
                        }
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
            displayResult(content, module);
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

}
