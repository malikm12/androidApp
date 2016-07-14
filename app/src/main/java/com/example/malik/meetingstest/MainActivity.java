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
    public String dailyIP = "http://192.168.1.26";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * onCreate contains the instructions that need to be carried out when the activity is loaded.
     * onRestart is a method that refreshes the activity when it is visited from another activity.
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
         * contents of the searchField as the parameters.Another toast will be displayed if the
         * search was unable to find any records that matched the criteria.
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
         * If there is no internet access the searchField and searchButton are disabled and the
         * searchField is populated with "NOT AVAILABLE OFFLINE".
         */

        searchField = (EditText) findViewById(R.id.searchField);
        if(isNetworkAvailable()==false){
            searchField.setText("NOT AVAILABLE OFFLINE");
            searchField.setEnabled(false);
            searchButton.setEnabled(false);

        }

        /**
         * btnMic represents the microphone bar at the bottom of the screen. It also tests for an
         * internet connection. If one cannot be found a Toast is shown to the user dictating that
         * voice is not supported in offline mode. If there is an internet connection, the method
         * startSpeechToText is called
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

        /**
         * This is a simple test for an internet connection that runs despite the user input. This
         * will inform the user off there connection status, it will use a Toast to inform the user
         * of their connection status.
         */

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

    /**
     * onRestart restores the default values of the MainActivity. This is done to show the effect of
     * the page refreshing when the user returns to it. There is no use case where the user would
     * require existing data on this activity.
     */

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

    /**
     *
     * @param title
     * @param data
     *
     * displayData is the method incharge of issuing the intent for moving to the next activity.
     * displayData passess the module name as the title parameter and the JSON Object or array that
     * is required for the ListView in the next activity. the .putExtra method allows data to be
     * passed between intents and this is how data is transfereed between intents/activities in this
     * application.
     */

    private void displayData(String title, String data){
        Intent dataDisplayIntent = new Intent(this,Meetings_Page.class);

        dataDisplayIntent.putExtra(EXTRA_MESSAGE, data);
        dataDisplayIntent.putExtra(EXTRA_MESSAGE1,title);
        startActivity(dataDisplayIntent);
    }

    /**
     * startSpeechToText is a key method in the speech-to-text translation in this app.This method
     * provides all the prerequisite conditions for the Speech-to-text activity. This is an android
     * library that offers this functionality and it utilises Googles speech-to-text engine. This
     * does mean that it requires an internet connection to be functional.The RecognizerIntent is
     * a dialogue box that appears to the user that will prompt them to speak.
     */

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say \"search\" followed by your request.");

        /**
         * startActivityForResult invokes the intent and passes the speechrecognition code as well
         * as the intent parameters.SPEECH_RECOGNITION_CODE allows onActivity result identify the
         * correct data.
         */

        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * onActivityResult retrieves the result from the speech-to-text intent and parses it as an
     * array list containing Strings called data and stores the data as a String in the first Index.
     * the command method is then called and the String of spoken words is sent as a parameter.
     */

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

    /**
     *
     * @param words
     *
     * command creates a String array out of the String returned from the speech-to-text activity.
     * to create the String the decompose method is called to split the string into keywords that
     * are later used in the method. restcall & searchRequested are initialised. The method works by
     * executing the appropriate task depending on the keyword recognised CREATE A BACKUP OPTION
     * INCASE THE PHRASE IS NOT UNDERSTOOD.
     */

    private void command(String words){

        String[] s = decompose(words);
        String searchTerm = "";

        AsyncRestRequest restcall = new AsyncRestRequest();
        SearchRequested searchRequested = new SearchRequested();

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
            for (int i = 1; i<s.length; i++){
                searchTerm += s[i] + " ";
            }
            searchTerm = searchTerm.trim();
            searchRequested.execute(searchTerm);
            String did = "";
        }
    }

    /**
     * @param unfiltered
     * @return
     *
     * Decompose returns a String array that receives a String as input and splits it by ""(space)
     * as the delimeter. The result is an array filled with single words.
     */

    private String[] decompose(String unfiltered){
        String[] s = unfiltered.split(" ", 0);

        for (int i=0;i<s.length;i++){
            //textView.setText(s[i]);
        }
        return s;
    }

    /**
     * AsyncRestRequest is an embedded class that runs Asynchronously to the main GUI thread,this
     * class is responsible for the GET request for the data the user wishes to use. It establishes
     * a connection and then reads the data utilising the bufferedReader and stores the content in
     * the reader String. Once the data is read the bufferedReader closes and the reader String is
     * converted into a JSON Object so the "data" JSON Array can be extracted. This contains all the
     * relevant data that is then processed.If/else statements are used to determine the module and
     * the SQLite database is then updated as appropriate for offline use.The objects in the JSON
     * Array are then looped through and the names are extracted and stored in content variable as
     * a String.
     *
     * The onPostExecute method is then invoked and the displayData method is called to start the
     * new intent leading to the next activity and the relevant data is passed through as a string.
     */

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONArray dataChunk = new JSONArray();

        @Override
        protected String doInBackground(String... params) {

            try {
                /**
                 * Setting up the connection using HttpURLConnection and establishing the request
                 * method as 'GET'
                 */
                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/module/"+params[0]);
                HttpURLConnection suiteConnection = (HttpURLConnection) url.openConnection();
                suiteConnection.setRequestMethod("GET");

                if (suiteConnection.getRequestMethod().equals("GET")){
                    /**
                     * Creates a bufferedReader that utilises an InputStreamReader containing the
                     * inputStream from the URL that has been connected to.
                     */
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suiteConnection.getInputStream()));

                    String line;
                    /**
                     * Loops through the bufferedReader storing the line value in the reader String
                     * one line at a time.
                     */
                    while ((line = bufferedReader.readLine()) != null) {
                        reader += (line + "\n");
                    }

                    bufferedReader.close();

                    JSONObject jObject = new JSONObject(reader);
                    dataChunk = jObject.getJSONArray("data");

                    if (params[0].equals("Accounts")) {
                        /**
                         * updateDatabase adds the dataChunk data to the SQLite database where the
                         * Type column is the same as Accounts or whatever the module name is.
                         */
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
            /**
             * Sends the information from the AsyncRestRequest back into the main GUI thread and
             * calls the displayData method to initiate the Meetings_Page activity.
             */

            displayData(result,dataChunk.toString());
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    /**
     * Similar to the AsyncRestRequest class, however this class utilises the REST API's search
     * function. It passes the search request and parameters through the URL. As with the
     * AsyncRestRequest class it reads the data in and stores it in a String and then creates a JSON
     * Object.
     * In the onPostExecute method, it tests to see if the search returned any results. If not,
     * a toast is generated and the user is informed that there were no matches found. If results
     * are found, they are passed to the main GUI thread by calling the displayData method.
     */

    private class SearchRequested extends AsyncTask<String,String,String> {

        String reader = "";
        JSONObject dataChunk = new JSONObject();
        //String query = searchField.getText().toString();

        @Override
        protected String doInBackground(String... params) {
            try {

                /**
                 * endodedQuery uses the URLEncoder to correctly format the search query in the URL.
                 * This simplifies the URL generation and reduces errors.
                 */
                String encodedQuery = URLEncoder.encode(params[0],"utf-8");
                /**
                 * URL contains the search parameters and query unlike the standard GET request
                 * (search?search_type=basic&query_string="+encodedQuery).
                 */

                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/search?search_type=basic&query_string="+encodedQuery);
                HttpURLConnection suiteConnection = (HttpURLConnection) url.openConnection();
                suiteConnection.setRequestMethod("GET");

                if (suiteConnection.getRequestMethod().equals("GET")){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suiteConnection.getInputStream()));

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        reader += (line + "\n");
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

        /**
         * Testing to see if the search returned any results if not a toast is shown to the user.
         * Otherwise the search results are passed to displayData.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            if(dataChunk.toString().equals("{}")){
                Context context = getApplicationContext();
                CharSequence text = "No results found";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                displayData(result, dataChunk.toString());
            }
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    /**
     *@return
     *
     * hasDatabaseBeenCreated is a boolean that tests if a database called OfflineStorage already
     * exists. If an instance of OfflineStorage exists, then it returns true. If there is no
     * instance of OfflineStorage then it will return false.
     */
    public boolean hasDatabaseBeenCreated() {
        //http://stackoverflow.com/a/17846791
        File dbFile = this.getDatabasePath("OfflineStorage");
        return dbFile.exists();
    }

    /**
     * @param module
     * @param json
     *
     * updateDatabase finds and opens OfflineStorage and issues the SQL statement to update the
     * OfflineJson table. It sets the Json column to the value of the json String passed with the
     * method where the type column is the same as the module String passed with the method. E.g.
     * change the value of json where type equals accounts.
     */
    public void updateDatabase(String module, String json) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("OfflineStorage", android.content.Context.MODE_PRIVATE, null);
        String query = "UPDATE OfflineJson SET Json = '"+json+"' where type = '"+module+"';";
        mydatabase.execSQL(query);
        mydatabase.close();

    }

    /**
     * createDatabase creates a database called OfflineStorage assuming one does not already exist,
     * it then creates a table called OfflineJson and populates it so that the type columns are
     * populated and the Json columns contain empty Arrays.
     */
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

    /**
     * dropTable is never used, but existed as part of development, it was used to eliminate any
     * incorrect instances of the OfflineJson table. Not used in final product but kept for future
     * purposes. It simply uses an SQL statement to Drop(Delete) the table.
     */

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
