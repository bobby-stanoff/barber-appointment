package vn.something.barberfinal.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerService {
    private static final String URL_STRING = "https://strongly-sure-dragon.ngrok-free.app/receive-qr-data";

    public static void sendQRConfirm(final String pageId, final String psid, final String apid) {
        // AsyncTask to handle the network operation in the background
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Create URL object
                    URL url = new URL(URL_STRING);

                    // Create HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    // Create the JSON object
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("pageid", pageId);
                        jsonObject.put("psid", psid);
                        jsonObject.put("apid", apid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Send the JSON data to the server
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(jsonObject.toString());
                    outputStream.flush();
                    outputStream.close();

                    // Get the response code
                    int responseCode = connection.getResponseCode();
                    Log.d("NetworkUtils", "Response Code: " + responseCode);

                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString(); // Return the server's response

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    Log.d("NetworkUtils", "Response: " + result);
                } else {
                    Log.d("NetworkUtils", "Error in request");
                }
            }

        }.execute();
    }
}
