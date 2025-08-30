package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getweather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line ="";
                while((line = reader.readLine()) != null){
                    result.append(line).append("\n");

                }
                return result.toString();

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result == null) {
                show.setText("City not found!");
                return;
            }
            try{
                JSONObject jsonObject = new JSONObject(result);

                // ✅ Check if response has "main"
                if (!jsonObject.has("main")) {
                    show.setText("Invalid city or API error!");
                    return;
                }

                JSONObject main = jsonObject.getJSONObject("main");
                String temp = main.getString("temp");
                String feels = main.getString("feels_like");
                String tempMax = main.getString("temp_max");
                String tempMin = main.getString("temp_min");
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                String description = weather.getString("description");

                String weatherInfo =
                        "Temperature: " + temp + "°C\n" +
                                "Feels Like: " + feels + "°C\n" +
                                "Max Temp: " + tempMax + "°C\n" +
                                "Min Temp: " + tempMin + "°C\n" +
                                "Pressure: " + pressure + " hPa\n" +
                                "Humidity: " + humidity + "%";


                show.setText(weatherInfo);
            }catch(Exception e){
                e.printStackTrace();
                show.setText("Error parsing weather!");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString().trim();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Added units=metric to get Celsius
                url = "https://api.openweathermap.org/data/2.5/weather?lat=57&lon=-2.15&appid=adf1c8ca9eeacdce25d23e9590382813&units=metric";
                new getweather().execute(url); // ✅ no .get()
            }
        });
    }
}
