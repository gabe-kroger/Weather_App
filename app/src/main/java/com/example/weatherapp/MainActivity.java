package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultText;


    public void weatherButton(View view) {
        Log.i("weather is", cityName.getText().toString());

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
    }



       public class DownloadTask extends AsyncTask<String, Void, String>{

           @Override
           protected String doInBackground(String... strings) {
               URL url = null;
               HttpURLConnection urlConnection = null;
               String result = "";
               try{
                   url = new URL(strings[0]);
                   urlConnection = (HttpURLConnection)url.openConnection();
                   InputStream inputStream = urlConnection.getInputStream();
                   InputStreamReader reader = new InputStreamReader(inputStream);
                   int data = reader.read();
                   while(data != -1){
                       char current = (char) data;
                       result += current;
                       data = reader.read();
                   }
                   return result;

               }catch(Exception e){
                   e.printStackTrace();
                   Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
               }
               return null;
           }

           @Override
           protected void onPostExecute(String result) {
               super.onPostExecute(result);

               try {
                   String message = "";
                   JSONObject jsonObject = new JSONObject(result);
                   String weatherInfo = jsonObject.getString("weather");
                   Log.i("Weather content", weatherInfo);
                   JSONArray jsonArray = new JSONArray(weatherInfo);
                   for (int i = 0; i <jsonArray.length() ; i++) {
                       JSONObject jsonPart = jsonArray.getJSONObject(i);
                       String main = "";
                       String description = "";
                       main = jsonPart.getString("main");
                       description = jsonPart.getString("description");

                       Log.i("main", main);
                       Log.i("description",description);

                       if(main != "" && description != ""){
                           message += main + ": " + description + "\r\n";
                       }

                   }
                   if(message != ""){
                       resultText.setText(message);
                   } else{
                       Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                   }


               } catch (JSONException e) {
                   Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
               }
           }
       }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.editText);
        resultText = (TextView)findViewById(R.id.textView);

    }
}




