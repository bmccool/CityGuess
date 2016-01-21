package mccool.brendon.cityguess;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContinueGameActivity extends AppCompatActivity {
    public final static String GUESS_CITY = "mccool.brendon.cityguess.GUESS_CITY";
    public final static String GUESS_DISTANCE = "mccool.brendon.cityguess.GUESS_DISTANCE";
    private String searchConstant1 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    private String secretCity2     = "Fort+Wayne,+IN";
    private String searchConstant3 = "&destinations=";
    private String guessCity4      = "South+Bend,+IN";
    private String searchConstant5 = "&units=imperial&departure_time=1541202457&traffic_model=best_guess&key=AIzaSyBLh82jk4R-Uphi7OitId9pTICi92zFxew";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the last guessed city distance
        // First find the distance of the last guess
        Intent intent = getIntent();
        String distance = intent.getStringExtra(ContinueGameActivity.GUESS_DISTANCE);
        // Then set the text view to that distance
        TextView textView = (TextView) findViewById(R.id.last_guess_distance);
        textView.setText(distance);

        // Set the secret city string so that the next guess can be executed
        secretCity2 = intent.getStringExtra(NewGameActivity.SECRET_CITY);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** Called when the user clicks the Enter button */
    public void continueGame(View view) {
        // Get the guess
        EditText editText = (EditText) findViewById(R.id.continue_message);
        String guess = editText.getText().toString();
        Log.d("APP", "GUESS IS " + guess);

        // Format the string
        guessCity4 = guess.replace(' ', '+');
        Log.d("APP", "FORMATTED GUESS IS " + guessCity4);
        Log.d("APP", "FORMATTED CITY  IS " + secretCity2);


        // Check the network connection
        ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("NETWORK", "IS_CONNECTED");
            // If the network is connected, download the webpage
            new DownloadWebpageTask().execute(searchConstant1 + secretCity2 + searchConstant3 + guessCity4 + searchConstant5);
        } else {
            Log.d("NETWORK", "NOT_CONNECTED");
        }

    }

    public void checkGuess(String string) {
        Log.d("CHECKGUESS", "STRING IS: " + string);
        Intent intent;
        if (string.contains(" ft")) {
            // GUESS IS CORRECT, game over
            intent = new Intent(this, GameOverActivity.class);
        } else {
            // GUESS IS INCORRECT, Continue game
            intent = new Intent(this, ContinueGameActivity.class);
            // Pack the distance into the intent, so that the next activity can display it
            intent.putExtra(GUESS_DISTANCE, string);
        }

        intent.putExtra(NewGameActivity.SECRET_CITY, secretCity2);
        startActivity(intent);
        finish();
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("ASYNC", "RESULT IS: " + result);
            checkGuess(result);
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 700;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("NETWORK", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                Log.d("NETWORK", "String is: " + contentAsString);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
//            Reader reader = null;
//            reader = new InputStreamReader(stream, "UTF-8");
//            char[] buffer = new char[len];
//            reader.read(buffer);
//            return new String(buffer);
            JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            try {
                return readMessage(reader);
            } finally {
                reader.close();
            }
        }

//        public String readMessagesArray(JsonReader reader) throws IOException {
//            List messages = new ArrayList();
//
//            reader.beginArray();
//            while (reader.hasNext()) {
//                messages.add(readMessage(reader));
//            }
//            reader.endArray();
//            return messages;
//        }

        public String readMessage(JsonReader reader) throws IOException {
            // Set the strings to NOT FOUND so that we will know if they are not found
            String destination = "NOT FOUND";
            String distance    = "NOT FOUND";
            while (reader.hasNext()) {
                Log.d("PARSING", "1");
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    Log.d("PARSING", "TOP LEVEL: " + name);
                    if (name.equals("rows")) {
                        Log.d("PARSING", "2");
                        reader.beginArray();
                        while (reader.hasNext()) {
                            Log.d("PARSING", "3");
                            reader.beginObject();
                            while (reader.hasNext()) {
                                name = reader.nextName();
                                if (name.equals("elements")) {
                                    Log.d("PARSING", "4");
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        Log.d("PARSING", "5");
                                        reader.beginObject();
                                        while (reader.hasNext()) {
                                            name = reader.nextName();
                                            if (name.equals("distance")) {
                                                Log.d("PARSING", "5");
                                                reader.beginObject();
                                                while (reader.hasNext()) {
                                                    name = reader.nextName();
                                                    if (name.equals("text")) {
                                                        distance = reader.nextString();
                                                    } else {
                                                        reader.skipValue();
                                                    }
                                                }
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                    }
                                } else {
                                    reader.skipValue();
                                }
                            }
                        }
                    } else if (name.equals("destination_addresses")){
                        destination = "";
                        reader.beginArray();
                        while (reader.hasNext()) {
                            destination = destination + reader.nextString();
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
            }
            return destination + "\n" + distance;
        }
    }
}
