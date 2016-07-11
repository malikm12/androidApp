package com.example.malik.meetingstest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    /**
     * Declare allvariables to be used in the MainActivity. dailyIP is a variable that changes on a
     * daily basis to help the development environment. As the CRM is stored in localhost, the
     * emulator needs to connect to the CRM that has a dynamic IP address. isNetworkAvailable()
     * is a boolean used to dictate if the system is currently able to access the internet it is
     * used primarily as a reference for when to enable or disable features.
     */

    private Button accountsButton, contactsButton, meetingsButton;
    private ImageButton btnMic, searchButton;
    private EditText searchField;
    public final static String EXTRA_MESSAGE = "com.example.malik.meetingstest.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.malik.meetingstest.MESSAGE1";
    public String dailyIP = "http://192.168.1.38";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * onCreate contains the instructions that need to be carried out when the activity is loaded.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test to see if a database exists, if not one is created and populated with row name values.
        //the values assosciated to the rows are instantiated as empty arrays.

        if(hasDatabaseBeenCreated() == false){
            createDatabase();
        }

        accountsButton = (Button) findViewById(R.id.accountsButton);
        accountsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * If there is no internet connection available the existing database is queried for
                 * everything from the OfflineJson table where the Type column value is 'Accounts'
                 * it then moves to the first row in the table and calls the displayData method and
                 * passes the content of the json column (a JSON Object) as a parameter. If there is
                 * an internet connection however, getAccounts (an instance of the AsyncRestRequest
                 * class) is called and the module name is passed through as a parameter.
                 *
                 * This comment is applicable to the three main buttons in the GUI (Accounts,Contacts
                 * and Meetings).
                 */
                if(isNetworkAvailable()== false){
                   try {
                       SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage",MODE_PRIVATE, null);
                       Cursor resultSet = mydatabase.rawQuery("Select * from OfflineJson where Type = 'Accounts';", null);
                       resultSet.moveToFirst();
                       System.out.println(resultSet.getString(1));
                       displayData("Accounts", resultSet.getString(1));
                   }
                   catch (Exception e){
                       System.out.println(e);
                   }
                }
                else {
                    AsyncRestRequest getAccounts = new AsyncRestRequest();
                    getAccounts.execute("Accounts");
                }
            }
        });

        contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()== false){
                    try {
                        SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage", MODE_PRIVATE, null);
                        Cursor resultSet = mydatabase.rawQuery("Select * from OfflineJson where Type = 'Contacts';", null);
                        resultSet.moveToFirst();
                        System.out.println(resultSet.getString(1));
                        displayData("Contacts", resultSet.getString(1));
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }
                else {
                    AsyncRestRequest getContacts = new AsyncRestRequest();
                    getContacts.execute("Contacts");
                }
            }
        });

        meetingsButton = (Button) findViewById(R.id.meetingsButton);
        meetingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()== false){
                    try {
                        SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage",MODE_PRIVATE, null);
                        Cursor resultSet = mydatabase.rawQuery("Select * from OfflineJson where Type = 'Meetings';", null);
                        resultSet.moveToFirst();
                        System.out.println(resultSet.getString(1));
                        displayData("Meetings", resultSet.getString(1));
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }
                else {
                    AsyncRestRequest getMeetings = new AsyncRestRequest();
                    getMeetings.execute("Meetings");
                }
            }
        });

        /**
         * The searchButton when clicked checks the value in the searchField, if the value is empty
         * ("") the user will see a Toast explaining that there is no search criteria. If the
         * searchField is populated search (an instance of SearchRequested) is called passing the
         * contents of the searchField as the parameters.
         */


        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchField.getText().toString();
                if(searchField.getText().toString().equals("")){
                    Context context = getApplicationContext();
                    CharSequence text = "No Search value found, please try again";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    SearchRequested search = new SearchRequested();
                    search.execute(query);
                }
            }
        });

        /**
         * If there is no internetr access the searchField and searchButton are disabled and the
         * searchField is populated with "NOT AVAILABLE OFFLINE".
         */

        searchField = (EditText) findViewById(R.id.searchField);
        if(isNetworkAvailable()==false){
            searchField.setText("NOT AVAILABLE OFFLINE");
            searchField.setEnabled(false);
            searchButton.setEnabled(false);

        }

        /**
         * btnMic represents the microphone bar at the bottom of the screen. It also tests to see
         */

        btnMic = (ImageButton) findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()==false){
                    Context context = getApplicationContext();
                    CharSequence text = "No voice support in offline mode";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    startSpeechToText();
                }
            }
        });


        if (isNetworkAvailable() == true){

            Context context = getApplicationContext();
            CharSequence text = "Online";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "You are Offline";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        onRestart();
    }

    public void onRestart(){
        super.onRestart();

        searchField = (EditText) findViewById(R.id.searchField);
        if(isNetworkAvailable()==false){
            searchField.setText("NOT AVAILABLE OFFLINE");
            searchField.setEnabled(false);
            searchButton.setEnabled(false);
        }
        else {
            searchButton.setEnabled(true);
            searchField.setEnabled(true);
            searchField.setText("");
            btnMic.setEnabled(true);
        }
    }

    private void displayData(String title, String data){
        Intent dataDisplayIntent = new Intent(this,Meetings_Page.class);

        dataDisplayIntent.putExtra(EXTRA_MESSAGE, data);
        dataDisplayIntent.putExtra(EXTRA_MESSAGE1,title);
        startActivity(dataDisplayIntent);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say \"search\" followed by your request.");

        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    command(text);

                }
                break;
            }
        }
    }

    private void command(String words){

        String[] s = decompose(words);

        AsyncRestRequest restcall = new AsyncRestRequest();
        SearchRequested searchRequested = new SearchRequested();
        searchField.setText(s[1]);

        if (s[0].equals("accounts")){
                restcall.execute("Accounts");
        }
        else if (s[0].equals("contacts")){
            restcall.execute("Contacts");

        }
        else if (s[0].equals("meetings")){
            restcall.execute("Meetings");
        }
        else if (s[0].equals("search")){
            searchRequested.execute();
        }
    }

    private String[] decompose(String unfiltered){
        String[] s = unfiltered.split(" ", 0);

        for (int i=0;i<s.length;i++){
            //textView.setText(s[i]);
        }
        return s;
    }



    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONArray dataChunk = new JSONArray();

        @Override
        protected String doInBackground(String... params) {
            //Check if online
                //If online
                //Else, load from database
            try {
                URL url = new URL(dailyIP+"/suiteRest/Api/V8/module/"+params[0]);
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
                        //Persists to the sqlite database

                        updateDatabase("Accounts",dataChunk.toString());

                        for (int i = 0; i <= dataChunk.length(); i++) {
                            JSONObject requested = dataChunk.getJSONObject(i);
                            content += requested.getString("name") + "\n";
                        }
                    }
                    else if (params[0].equals("Contacts")){
                        updateDatabase("Contacts",dataChunk.toString());
                        for (int i = 0; i <= dataChunk.length(); i++) {
                            JSONObject requested = dataChunk.getJSONObject(i);
                            content += requested.getString("name") + "\n";
                        }
                    }
                    else if (params[0].equals("Meetings")){
                        updateDatabase("Meetings",dataChunk.toString());
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

    private class SearchRequested extends AsyncTask<String,String,String> {

        String reader = "";
        JSONObject dataChunk = new JSONObject();
        //String query = searchField.getText().toString();

        @Override
        protected String doInBackground(String... params) {
            try {


                String encodedQuery = URLEncoder.encode(params[0],"utf-8");

                URL url = new URL(dailyIP+"/suiteRest/Api/V8/search?search_type=basic&query_string="+encodedQuery);
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
                    dataChunk = jObject.getJSONObject("data");



                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            return "Search Results";
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
    public boolean hasDatabaseBeenCreated() {
        //http://stackoverflow.com/a/17846791
        File dbFile = this.getDatabasePath("OfflineStorage");
        return dbFile.exists();
    }
    public void updateDatabase(String module, String json) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage", android.content.Context.MODE_PRIVATE, null);
        String query = "UPDATE OfflineJson SET Json = '"+json+"' where type = '"+module+"';";
        mydatabase.execSQL(query);
        mydatabase.close();

    }

    public void createDatabase(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage", MODE_PRIVATE, null);
        try {
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS OfflineJson(Type VARCHAR,Json VARCHAR);");
            mydatabase.execSQL("INSERT INTO OfflineJson VALUES('Accounts','[]');");
            mydatabase.execSQL("INSERT INTO OfflineJson VALUES('Contacts','[]');");
            mydatabase.execSQL("INSERT INTO OfflineJson VALUES('Meetings','[]');");

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void dropTable(){
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage", MODE_PRIVATE, null);
            mydatabase.execSQL("DROP TABLE OfflineJson;");
        }
        catch(Exception err)
        {
            System.err.println(err.getMessage());
        }
    }
}
