package com.example.malik.meetingstest;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;



public class AccountResult extends AppCompatActivity {

    private Button backButton,updateButton, editButton, deleteButton;
    private ImageButton gMapsButton, playAudio;
    private TextView infoViewer, titleViewer, headerView, moduleView, headerTitle, bonusView, textView2, contactTitle;
    private String words,body = "", moduleTitle,parentID = "", dailyIP = "http://192.168.1.26";
    public final static String EXTRA_MESSAGE = "com.example.malik.meetingstest.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.malik.meetingstest.MESSAGE1";
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


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

        moduleTitle = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE1);
        moduleView = (TextView)findViewById(R.id.moduleView);
        titleViewer = (TextView)findViewById(R.id.titleViewer);
        headerTitle = (TextView)findViewById(R.id.headerTitle);
        headerView = (EditText) findViewById(R.id.headerView);
        contactTitle = (TextView) findViewById(R.id.contactTitle);
        bonusView = (TextView) findViewById(R.id.bonusView);
        textView2 = (TextView) findViewById(R.id.textView2);
        infoViewer = (EditText) findViewById(R.id.infoViewer);
        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()==false){
                    toaster(null);
                }
                else {

                    headerView.setEnabled(true);
                    if (moduleTitle.equals("Accounts")) {
                        infoViewer.setEnabled(false);

                    }

                    else if (moduleTitle.equals("Contacts")) {
                        infoViewer.setEnabled(false);
                    }

                    else{
                        infoViewer.setEnabled(true);
                        updateButton.setClickable(true);
                        editButton.setClickable(false);
                    }
                }
            }
        });

        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()==false){
                    toaster(null);
                }
                else {
                    headerView.setEnabled(false);
                    infoViewer.setEnabled(false);
                    updateButton.setClickable(false);
                    editButton.setClickable(true);
                    UpdateDataRequest update = new UpdateDataRequest();
                    update.execute();
                }

            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()==false){
                    toaster(null);
                }
                else {
                    UpdateDelete delete = new UpdateDelete();
                    delete.execute();
                    AsyncRestRequest getAccounts = new AsyncRestRequest();
                    getAccounts.execute(moduleTitle);
                    finish();
                    finish();
                }
            }
        });


        titleViewer.setText(bits[0]);
        headerView.setText(bits[1]);

        if(moduleTitle.equals("Accounts")){
            moduleView.setText("Account Name:");
            headerTitle.setText("Website");
            contactTitle.setText("Phone:");
            bonusView.setText(bits[2]);
            textView2.setText("Address:");
        }
        else if (moduleTitle.equals("Contacts")){
            moduleView.setText("Contact Name:");
            headerTitle.setText("Email:");
            contactTitle.setText("Phone:");
            bonusView.setText(bits[2]);
            textView2.setText("Address:");
        }
        else if (moduleTitle.equals("Meetings")){
            moduleView.setText("Meeting:");
            headerTitle.setText("Subject:");
            contactTitle.setText("Contact:");
            bonusView.setText(bits[2]);
            textView2.setText("Notes:");
        }
        else if (moduleTitle.equals("Leads")){
            moduleView.setText("Lead:");
            headerTitle.setText("Account:");
            contactTitle.setText("Title");
            bonusView.setText(bits[2]);
            textView2.setText("Phone:");
        }

        if (contactTitle.getText().equals("Phone:")){
            bonusView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            bonusView.setFocusable(false);
            bonusView.setEnabled(true);
            bonusView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    Intent diallerIntent = new Intent(Intent.ACTION_DIAL);
                    diallerIntent.setData(Uri.parse("tel:"+bonusView.getText().toString()));
                    startActivity(diallerIntent);
                }
            });

        }

        if (textView2.getText().equals("Address:")) {
                for (int i = 3; i < bits.length-1; i++) {
                    body += bits[i] + "\n";
                }
        }

        else if (textView2.getText().equals("Notes:")) {
                for (int i = 3; i < bits.length - 1; i++) {
                    body += bits[i] + "\n";
                }
        }

        else if (textView2.getText().equals("Phone:")) {

            body += bits[3];
            infoViewer.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            infoViewer.setFocusable(false);
            infoViewer.setEnabled(true);
            infoViewer.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        Intent diallerIntent = new Intent(Intent.ACTION_DIAL);
                        diallerIntent.setData(Uri.parse("tel:"+infoViewer.getText().toString()));
                        startActivity(diallerIntent);
                    }
                });

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
                if(isNetworkAvailable()==false){
                    Context context = getApplicationContext();
                    CharSequence text = "Maps not available offline";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    if (moduleTitle.equals("Contacts")){
                        NewMapRequest contactMap = new NewMapRequest();
                        contactMap.execute(parentID);
                    }
                    else {
                        NewDataRequest mapLocation = new NewDataRequest();
                        mapLocation.execute(parentID);
                    }
                }
            }
        });


    }


    private void onMap(String location){

        Uri mapsIntentUri = Uri.parse("geo:0,0?q=".concat(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
        startActivity(mapIntent);
    }

    private void displayData(String title, String data){
        Intent dataDisplayIntent = new Intent(this,Meetings_Page.class);

        dataDisplayIntent.putExtra(EXTRA_MESSAGE, data);
        dataDisplayIntent.putExtra(EXTRA_MESSAGE1,title);
        dataDisplayIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dataDisplayIntent);
    }



    private class NewDataRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONObject fields = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/module/Accounts/"+params[0]);
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
                            content += fields.getString("billing_address_postalcode") + "\n";
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

    private class NewMapRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONObject fields = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/module/Contacts/"+params[0]);
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
                        content += fields.getString("primary_address_street") + "\n";
                        content += fields.getString("primary_address_city") + "\n";
                        content += fields.getString("primary_address_postalcode") + "\n";
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
                    jsonObject.put("email1",upateField1);
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
                System.out.println(httpResponse.getStatusLine().getStatusCode());

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


    private class UpdateDelete extends AsyncTask<String,String,String> {

        String idNum = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE2);
        String module = getIntent().getStringExtra(Meetings_Page.EXTRA_MESSAGE1);
        String deleteID = "1";
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
                jsonObject.put("deleted", deleteID);
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

            Context context = getApplicationContext();
            CharSequence text = "Marked for deletion";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONArray dataChunk = new JSONArray();

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/module/"+params[0]);
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
                    dataChunk = jObject.getJSONArray("data");

                    if (params[0].equals("Accounts")) {

                        for (int i = 0; i <= dataChunk.length(); i++) {
                            JSONObject requested = dataChunk.getJSONObject(i);
                            content += requested.getString("name") + "\n";
                        }
                    }
                    else if (params[0].equals("Contacts")){
                        for (int i = 0; i <= dataChunk.length(); i++) {
                            JSONObject requested = dataChunk.getJSONObject(i);
                            content += requested.getString("name") + "\n";
                        }
                    }
                    else if (params[0].equals("Meetings")){
                        for (int i = 0; i <= dataChunk.length(); i++) {
                            JSONObject requested = dataChunk.getJSONObject(i);
                            content += requested.getString("parent_name") + "\n";
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
            displayData(result,dataChunk.toString());
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    public void toaster (String situation){

        Context context = getApplicationContext();
        CharSequence text = "Cannot amend record offline!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void goback() {
        finish();
    }
    private void refresh(){
        finish();
        startActivity(getIntent());
    }


}
