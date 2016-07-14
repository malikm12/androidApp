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
    private ImageButton gMapsButton, playAudio, navigateButton, callButton, webButton,emailButton;
    private TextView infoViewer, titleViewer, moduleView, headerTitle, textView2, contactTitle;
    private EditText bonusView, headerView;
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
        bonusView = (EditText) findViewById(R.id.bonusView);
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
               else{
                   headerView.setEnabled(true);
                   bonusView.setEnabled(true);
                   titleViewer.setEnabled(true);
                   infoViewer.setEnabled(true);
                   updateButton.setClickable(true);
                   editButton.setClickable(false);


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
                    bonusView.setEnabled(false);
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
            headerTitle.setText("Date & Time");
            headerView.setText(bits[3]);
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

        if (textView2.getText().equals("Address:")) {
                for (int i = 3; i < bits.length-1; i++) {
                    body += bits[i] + "\n";
                }
        }

        else if (textView2.getText().equals("Notes:")) {
                for (int i = 4; i < bits.length - 1; i++) {
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
            public void onClick (View v) {
                if (isNetworkAvailable() == false) {
                    Context context = getApplicationContext();
                    CharSequence text = "Maps not available offline";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    if (moduleTitle.equals("Contacts")) {
                        NewMapRequest contactMap = new NewMapRequest();
                        contactMap.execute(parentID);
                    } else {
                        NewDataRequest mapLocation = new NewDataRequest();
                        mapLocation.execute(parentID);
                        System.out.println(parentID);
                    }
                }
            }


        });

        callButton = (ImageButton) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(moduleTitle.equals("Meetings")){
                    CallRequest callRequest = new CallRequest();
                    callRequest.execute(parentID);
                }
                else {
                    String number = bonusView.getText().toString();
                    Intent diallerIntent = new Intent(Intent.ACTION_DIAL);
                    diallerIntent.setData(Uri.parse("tel:" + number));
                    startActivity(diallerIntent);
                }
            }
        });

        emailButton = (ImageButton) findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if (moduleTitle.equals("Contacts")) {
                    String emailAddress = headerView.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(intent.EXTRA_EMAIL, emailAddress);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Our Meeting");
                    intent.putExtra(Intent.EXTRA_TEXT, "I am running 5 minutes late");
                    Intent mailer = Intent.createChooser(intent, null);
                    startActivity(mailer);
                }
                else {
                    EmailTo emailTo = new EmailTo();
                    emailTo.execute(parentID);
                }
            }
        });

        webButton = (ImageButton) findViewById(R.id.webButton);
        webButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                String url = "http://"+headerView.getText().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        if(moduleTitle.equals("Accounts")){
            emailButton.setVisibility(View.INVISIBLE);
        }

        navigateButton = (ImageButton) findViewById(R.id.navigateButton);
        navigateButton.setVisibility(View.INVISIBLE);
        if (moduleTitle.equals("Meetings")){
            navigateButton.setVisibility(View.VISIBLE);
        }
        navigateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(isNetworkAvailable()==false){
                    Context context = getApplicationContext();
                    CharSequence text = "Navigation not available offline";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                        NewDataRequest mapLocation = new NewDataRequest();
                        mapLocation.execute(parentID,"navigate to ");

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
        String result = "";

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
            if(params.length == 2){
                result = "navigate to ";
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            if(result.equals("navigate to ")){
                content = result + content;
            }
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
        String updateField1 = headerView.getText().toString();
        String updateField2 = bonusView.getText().toString();

        int responseCode;

        @Override
        protected String doInBackground(String... params) {
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPut httpPUT = new
                        HttpPut("http://dev.suitecrm.com/maliksInstance/Api/V8/module/" + module +"/" + idNum);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                if (module.equals("Meetings")) {
                    jsonObject.put("description", updateField);
                    jsonObject.put("assigned_user_name", updateField2);
                }
                else if (module.equals("Accounts")){
                    String [] address = updateField.split("\n");
                    jsonObject.put ("website",updateField1);
                    jsonObject.put ("phone_office",updateField2);
                    jsonObject.put ("billing_address_street",address[0]);
                    jsonObject.put ("billing_address_city",address[1]);
                    jsonObject.put ("billing_address_state", address[2]);
                    jsonObject.put ("billing_address_postalcode", address[3]);
                    jsonObject.put ("billing_address_country", address[4]);
                }
                else if (module.equals("Contacts")){
                    jsonObject.put("email1",updateField1);
                    String [] address = updateField.split("\n");
                    jsonObject.put ("website",updateField1);
                    jsonObject.put ("primary_address_street",address[0]);
                    jsonObject.put ("primary_address_city",address[1]);
                    jsonObject.put ("primary_address_state", address[2]);
                    jsonObject.put ("primary_address_postalcode", address[3]);
                    jsonObject.put ("primary_address_country", address[4]);
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
                        HttpPut("http://dev.suitecrm.com/maliksInstance/Api/V8/module/" + module +"/" + idNum);
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

    private class CallRequest extends AsyncTask<String,String,String> {

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
                        content += fields.getString("phone_office") + "\n";

                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String result) {
            String number = result;
            Intent diallerIntent = new Intent(Intent.ACTION_DIAL);
            diallerIntent.setData(Uri.parse("tel:"+number));
            startActivity(diallerIntent);
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
    private class EmailTo extends AsyncTask<String,String,String> {

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
                        content += fields.getString("email_addresses_primary") + "\n";

                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String result) {
            String emailAddress = result;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(intent.EXTRA_EMAIL,emailAddress);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Our Meeting");
            intent.putExtra(Intent.EXTRA_TEXT, "I am running 5 minutes late");
            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);

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
