package com.example.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String city;
    String OPEN_WEATHER_MAP_API = "d3ce0c96d6214a118319866bc7dfdad3";
    private Button Search;
    private TextView Status;
    private TextView Temperature;
    private TextView Date;
    private TextView Humidity;
    private TextView Pressure;
    private TextView Location;
    public EditText SearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location = (TextView) findViewById(R.id.location);
        Temperature = (TextView) findViewById(R.id.temperature);
        Status = (TextView) findViewById(R.id.status);
        Date = (TextView) findViewById(R.id.dateTime);
        Humidity = (TextView) findViewById(R.id.humidity);
        Pressure = (TextView) findViewById(R.id.pressure);
        Search = (Button) findViewById(R.id.searchButton);
        SearchBar = (EditText) findViewById(R.id.searchBar);

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = SearchBar.getText().toString(); // takes user input and stores to be used in function
                taskLoadUp(city);
            }
        });

    }
    // First check if there is an internet connnection then proceed to download weather information
    public void taskLoadUp(String query) { //
        if (Connection.networkBool(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String...args) {
            String xml = Connection.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            try {
                //Creating javascript object to be able to get and store downloaded information from api call
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    //Update textviews with new information
                    Location.setText(city);
                    Status.setText(details.getString("description").toUpperCase(Locale.US));
                    Temperature.setText(String.format("%.2f", main.getDouble("temp")) + "°c");
                    Humidity.setText("Humidity: " + main.getString("humidity") + "%");
                    Pressure.setText("Pressure: " + main.getString("pressure") + " hPa");
                    Date.setText(df.format(new Date(json.getLong("dt") * 1000)));

                }
            } catch (JSONException e) {
                //when search bar doesnt find city
                Toast.makeText(getApplicationContext(), "Invalid city", Toast.LENGTH_SHORT).show();
            }


        }



    }


}
