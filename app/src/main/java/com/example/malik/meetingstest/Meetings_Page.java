package com.example.malik.meetingstest;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Iterator;

/**
 * Meetings_Page displays a ListView populated by information relevant to the user, the items in the
 * ListView are clickable and lead the user to a result page. Information is passed between activities
 * via intents. The user can navigate back to the Main activity, to the AccountResult Activity or to
 * the CreationScreen Activity from this activity.
 */


public class Meetings_Page extends AppCompatActivity {

    /**
     * Declaration of Global variables within the class.
     * isNetworkAvailable()
     * is a boolean used to dictate if the system is currently able to access the internet it is
     * used primarily as a reference for when to enable or disable features.
     */
    private Button backButton, createButton;
    private ListView list;
    private TextView dataTitle;
    private String module;
    public final static String EXTRA_MESSAGE = "com.example.malik.meetingstest.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.malik.meetingstest.MESSAGE1";
    public final static String EXTRA_MESSAGE2 = "com.example.malik.meetingstest.MESSAGE2";
    public final static String EXTRA_MESSAGE3 = "com.example.malik.meetingstest.MESSAGE3";
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * onCreate method initialises all the activity components and populates them with the relevant
     * data.ArrayLists are built to carry and pair information in the list. The list is populated
     * by looping through the JSON Array and extracting particular objects at each entry of the
     * JSON Array. For example the JSON Array could contain several records(objects) it would then
     * loop through and at each object extract an object (e.g."name") and assign it to an array list.
     * The list then uses an adapter to apply the values in the array with the entries on the list.
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings__page);
        onRestart();


        dataTitle = (TextView) findViewById(R.id.dataTitle);
        dataTitle.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE1));

        module = (String) dataTitle.getText();

        final ArrayList<String> values = new ArrayList<String>();


        list = (ListView) findViewById(R.id.listView);
       final String data = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);

        final ArrayList<String> ids = new ArrayList<String>();
        final ArrayList<String> keys = new ArrayList<String >();

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
        else if (dataTitle.getText().equals("Search Results")) {
            try {
                JSONObject obj = new JSONObject(data);
                Iterator<String> iter = obj.keys();
                values.clear();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = (JSONObject)obj.get(key);
                        JSONArray matches = value.getJSONArray("items");

                        for (int i = 0; i<=matches.length();i++){
                            JSONObject searchName = matches.getJSONObject(i);
                            values.add(key +": "+ searchName.getString("summary"));
                            ids.add(searchName.getString("id"));
                            keys.add(searchName.getString("moduleName"));
                        }
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }
            }
            catch (Exception e){

            }
        }


            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * setOnItemClickListener is responsible for calling hitResult, that takes the data and
             * presents it in a new activity.If the result was found through a search result then
             * key paired with the value in the JSON Object needs to be extracted to pass through as
             * the module variable.
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String accountId = "";
                String mod = "";

                try {
                    accountId = ids.get(position);

                }
                catch(Exception err)
                {
                    System.out.println(err.getMessage());
                }
               try {
                   if (module.equals("Search Results")){
                       mod = keys.get(position);
                       module = mod;
                       hitResult(accountId,module,position);

                   }
                   else {
                       hitResult(accountId, module,position);
                   }
               }catch (Exception e){
                   System.out.println(e);
               }

            }

        });


        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
            }
        });

        /**
         * createButton is responsible for opening the CreationScreen Activity. When clicked it
         * checks for an internet connection and reacts appropriately. No internet connection
         * results in a Toast Telling the user that creation is not available while offline. If an
         * internet connection is established but the user is on the Search results Meetings_Page
         * then another Toast will be tgenerated saying that the user is on an invalid screen and to
         * make a selection from the home screen in order to create a record. If there is a
         * connection and the user is on a module Meetings_Page an intent is called to pass the user
         * to the relevant page.
         */
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()==false){
                    Context context = getApplicationContext();
                    CharSequence text = "Cannot create records in offline mode. Please try again when you have an internet connection.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    if(dataTitle.getText().toString().equals("Search Results")){
                        Context context = getApplicationContext();
                        CharSequence text = "Invalid type selected, please create records through module selection on the home screen";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else {
                        Intent gotoCreate = new Intent(Meetings_Page.this, CreationScreen.class);
                        gotoCreate.putExtra(EXTRA_MESSAGE3, dataTitle.getText());
                        startActivity(gotoCreate);
                    }
                }
            }
        });

    }

    /**
     * Refreshes the defaults of the meetings page to prevent any data being kept over.
     */

    public void onRestart(){
        super.onRestart();
        dataTitle = (TextView) findViewById(R.id.dataTitle);
        dataTitle.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE1));

        module = (String) dataTitle.getText();

        final ArrayList<String> values = new ArrayList<String>();


        list = (ListView) findViewById(R.id.listView);
        final String data = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);

        final ArrayList<String> ids = new ArrayList<String>();
        final ArrayList<String> keys = new ArrayList<String >();

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
        else if (dataTitle.getText().equals("Search Results")) {
            try {
                JSONObject obj = new JSONObject(data);
                Iterator<String> iter = obj.keys();
                values.clear();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = (JSONObject)obj.get(key);
                        JSONArray matches = value.getJSONArray("items");

                        for (int i = 0; i<=matches.length();i++){
                            JSONObject searchName = matches.getJSONObject(i);
                            values.add(key +": "+ searchName.getString("summary"));
                            ids.add(searchName.getString("id"));
                            keys.add(searchName.getString("moduleName"));
                        }
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }
            }
            catch (Exception e){

            }
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        // ListView Item Click Listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String accountId = "";
                String mod = "";

                try {

                    accountId = ids.get(position);
                    mod = keys.get(position);
                }
                catch(Exception err)
                {
                    System.out.println(err.getMessage());
                }
                try {
                    if (module.equals("Search Results")){
                        module = mod;
                        hitResult(accountId,module,position);

                    }
                    else {
                        hitResult(accountId, module,position);
                    }
                }catch (Exception e){
                    System.out.println(e);
                }

            }

        });


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
                if(isNetworkAvailable()==false){
                    Context context = getApplicationContext();
                    CharSequence text = "Cannot create records in offline mode. Please try again when you have an internet connection.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    Intent gotoCreate = new Intent(Meetings_Page.this, CreationScreen.class);
                    gotoCreate.putExtra(EXTRA_MESSAGE3, dataTitle.getText());
                    startActivity(gotoCreate);
                }
            }
        });

    }

    /**
     * goMain terminates the current Activity and goes to the last in the stack.
     */

    private void goMain() {
        finish();
    }

    /**
     * displayResult is the method in charge of issuing the intent for moving to the next activity.
     * displayResult passes the Extracted string as content, the module name as module and the
     * id number of the records account as idNum. This information is used to display the
     * appropriate data in the AccountResult Activity.The id number is passed so future calls can
     * be made on the data in the AccountResult Activity.
     * @param content
     * @param module
     * @param idNum
     */
    private void displayResult(String content, String module,String idNum){
        Intent gotoResult = new Intent(this, AccountResult.class);
        gotoResult.putExtra(EXTRA_MESSAGE, content);
        gotoResult.putExtra(EXTRA_MESSAGE1,module);
        gotoResult.putExtra(EXTRA_MESSAGE2, idNum);
        startActivity(gotoResult);
    }

    /**
     *
     * @param accountID
     * @param module
     * @param position
     */

    private void hitResult(String accountID,String module, int position) {
        final String data = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(isNetworkAvailable()==false){
            displayResult(sortData(data,position),module,accountID);
        }
        else {
            AsyncRestRequest singularCall = new AsyncRestRequest();
            singularCall.execute(accountID);
        }
    }

    private class AsyncRestRequest extends AsyncTask<String,String,String> {

        String reader = "";
        String content = "";
        JSONObject fields = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://dev.suitecrm.com/maliksInstance/Api/V8/module/"+ module +"/"+params[0]+"?XDEBUG_SESSION_START=PHPSTORM");
               // http://dev.suitecrm.com/maliksInstance/Api/V8/module/Accounts
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
                    //sortData(reader,);

                    if (module.equals("Accounts")){

                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields != null) {
                            content += fields.getString("name") + "\n";
                            content += fields.get("website") + "\n";
                            content += fields.get("phone_office") + "\n";
                            content += fields.getString("billing_address_street") + "\n";
                            content += fields.getString("billing_address_city") + "\n";
                            content += fields.getString("billing_address_state") + "\n";
                            content += fields.getString("billing_address_postalcode") + "\n";
                            content += fields.getString("billing_address_country") + "\n";
                            content += fields.getString("id") + "\n";
                        }
                    }

                    else if (module.equals("Contacts")){
                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("name") + "\n";
                            content += fields.getString("email1") + "\n";
                            content += fields.getString("phone_work") + "\n";
                            content += fields.getString("primary_address_street") + "\n";
                            content += fields.getString("primary_address_city") + "\n";
                            content += fields.getString("primary_address_state") + "\n";
                            content += fields.getString("primary_address_postalcode") + "\n";
                            content += fields.getString("primary_address_country") + "\n";
                            content += fields.getString("id") + "\n";
                        }
                    }

                    else if (module.equals("Meetings")){
                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("parent_name") + "\n";
                            content += fields.getString("name") + "\n";
                            content += fields.getString("assigned_user_name") + "\n";
                            content += fields.getString("date_start") + "\n";
                            content += fields.getString("description") + "\n";
                            content += fields.getString("parent_id") + "\n";
                        }
                    }
                    else if (module.equals("Leads")){
                        JSONObject jObject = new JSONObject(reader);
                        fields = jObject.getJSONObject("data");
                        if (fields!=null) {
                            content += fields.getString("name") + "\n";
                            content += fields.getString("account_name") + "\n";
                            content += fields.getString("title") + "\n";
                            content += fields.getString("phone_work") + "\n";
                            content += fields.getString("email_addresses_primary") + "\n";
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
            displayResult(content, module, result);
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private String sortData (String reader,int position) {
        String content = "";
        JSONObject fields;


        try {
            if (module.equals("Accounts")) {

                JSONArray jsonArray = new JSONArray(reader);
                fields = jsonArray.getJSONObject(position);
                if (fields != null) {
                    content += fields.getString("name") + "\n";
                    content += fields.get("website") + "\n";
                    content += fields.get("phone_office") + "\n";
                    content += fields.getString("billing_address_street") + "\n";
                    content += fields.getString("billing_address_city") + "\n";
                    content += fields.getString("billing_address_state") + "\n";
                    content += fields.getString("billing_address_postalcode") + "\n";
                    content += fields.getString("billing_address_country") + "\n";
                   // content += fields.getString("id") + "\n";
                }
            } else if (module.equals("Contacts")) {
                JSONArray jsonArray = new JSONArray(reader);
                fields = jsonArray.getJSONObject(position);
                if (fields != null) {
                    content += fields.getString("name") + "\n";
                    content += fields.getString("email1") + "\n";
                    content += fields.getString("phone_work") + "\n";
                    content += fields.getString("primary_address_street") + "\n";
                    content += fields.getString("primary_address_city") + "\n";
                    content += fields.getString("primary_address_state") + "\n";
                    content += fields.getString("primary_address_postalcode") + "\n";
                    content += fields.getString("primary_address_country") + "\n";
                   // content += fields.getString("id") + "\n";
                }
            } else if (module.equals("Meetings")) {
                JSONArray jsonArray = new JSONArray(reader);
                fields = jsonArray.getJSONObject(position);
                if (fields != null) {
                    content += fields.getString("parent_name") + "\n";
                    content += fields.getString("name") + "\n";
                    content += fields.getString("assigned_user_name") + "\n";
                    content += fields.getString("description") + "\n";
                    content += fields.getString("parent_id") + "\n";
                }
            } else if (module.equals("Leads")) {
                JSONArray jsonArray = new JSONArray(reader);
                fields = jsonArray.getJSONObject(position);
                if (fields != null) {
                    content += fields.getString("name") + "\n";
                    content += fields.getString("account_name") + "\n";
                    content += fields.getString("title") + "\n";
                    content += fields.getString("phone_work") + "\n";
                    content += fields.getString("email_addresses_primary") + "\n";
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return content;


    }

}
