package com.example.joyrasmussen.tripmessenger;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by clay on 4/29/17.
 */

public class DownloadTask extends AsyncTask<String, Void, String>{

    RouteActivity activity;

    public DownloadTask(RouteActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {

            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while (data != -1){

                char current = (char) data;
                result += current;

                data = reader.read();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {

            Log.i("loading", "done");
            JSONObject root = new JSONObject(s);

            JSONObject route = root.getJSONArray("routes").getJSONObject(0);

            Double northeastLat = Double.parseDouble(route.getJSONObject("bounds").getJSONObject("northeast").getString("lat"));
            Double northeastLng = Double.parseDouble(route.getJSONObject("bounds").getJSONObject("northeast").getString("lng"));
            Double southwestLat = Double.parseDouble(route.getJSONObject("bounds").getJSONObject("southwest").getString("lat"));
            Double southwestLng = Double.parseDouble(route.getJSONObject("bounds").getJSONObject("southwest").getString("lng"));

            String polyline = route.getJSONObject("overview_polyline").getString("points");

            activity.drawPolyline(polyline, northeastLat, northeastLng, southwestLat, southwestLng);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
