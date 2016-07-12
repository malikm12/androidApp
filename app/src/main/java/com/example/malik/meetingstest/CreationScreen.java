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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_creation_screen);
            /**
             * Declare all the GUI components and make them reachable by the program
             */

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

            /**
             * Depending on the Title page, populate the labels for each EditText differently.
             * editable1_5 is only used for the surname entry in the Contact creation screen.
             * editable5 is not used in contacts but it is in the others. This explains the
             * setEnabled(false) calls.
             */

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

            /**
             * createButton calls an AsyncRestRequest unique to this page. It is responsible for
             * a HTTP POST call that places new information on th CRM. It then terminates the
             * CreationScreen activity and returns the user to the previous activity.
             */
            createButton = (Button) findViewById(R.id.createButton);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncRestRequest created = new AsyncRestRequest();
                    created.execute();
                    goMain();
                }
            });
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * goMain is responsible for terminating the current activity and returning the user to the
     * previous activity in the stack.
     */
    private void goMain() {
        finish();
    }

    private class AsyncRestRequest extends AsyncTask<String,String,String> {
        /**
         * Declaring all the variables to be used in this class.
         */
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
                /**
                 * Utilises HttpClient instead of HttpURLConnection as is clear on how this works
                 * could be changed in future for a more uniform looking code. This library was used
                 * for both PUT and POST calls.
                 *
                 * This call creates a JSON Object and populates it with the information submitted
                 * by the user. This JSON Object is then converted into a String to be stored in the
                 * request body.StringEntity is how the httpPOST request carries the data.
                 *
                 * The httpPOST is then executed and the response code is extracted as to provide
                 * validation of the result.It is converted to a string and passed to the
                 * onPostExecute method.
                 */
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPOST = new HttpPost("http://dev.suitecrm.com/maliksInstance/Api/V8/module/" + module);
                String json;
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

                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPOST.setEntity(se);

                HttpResponse httpResponse = httpclient.execute(httpPOST);
                responseCode = httpResponse.getStatusLine().getStatusCode();

            }
            catch (Exception e){
                System.out.println(e);
            }

            String respCode = Integer.toString(responseCode);

            return respCode;
        }

        /**
         * @param responseCode
         *
         * onPostExecute tests the responseCode generated by the httpPOST Request. If a 200 code is
         * returned, the POST has been successful and a Toast informing the user is generated. If
         * any other code is returned, it is assumed that the creation failed and the User is
         * encouraged to try again.
         *
         */
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
