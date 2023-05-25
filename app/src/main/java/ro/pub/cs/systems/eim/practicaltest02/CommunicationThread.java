package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (key / value / information type!");

            HashMap<String, String> data = serverThread.getData();

            // Read the city and informationType values sent by the client
            String operation = bufferedReader.readLine();
            String key;
            String value;
            if (operation.compareTo("get") == 0) {
                key = bufferedReader.readLine();

                if (key == null || key.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / information type!");
                }

                //make the HTTP request to the web service
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                String pageSourceCode = "";
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();

                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);

                JSONObject content = new JSONObject(pageSourceCode);
                String time = content.getString("unixtime");

                String result = "";

                if(Long.parseLong(time) - ServerThread.getTime() > 10) {
                    result = "expired\n";
                    ServerThread.deleteData();

                    printWriter.println(result);
                    printWriter.flush();
                } else {
                    result = data.get(key);

                    printWriter.println(result);
                    printWriter.flush();
                }
            } else if (operation.compareTo("put") == 0) {
                key = bufferedReader.readLine();
                value = bufferedReader.readLine();

                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value / information type!");
                }

                //make the HTTP request to the web service
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                String pageSourceCode = "";
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();

                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);

                JSONObject content = new JSONObject(pageSourceCode);
                String time = content.getString("unixtime");

                serverThread.setTime(Long.parseLong(time));

                serverThread.setData(key, value);

                printWriter.println("put success");
                printWriter.flush();
            }

            // It checks whether the serverThread has already received the weather forecast information for the given city.
//            HashMap<String, String> data = serverThread.getData();
//            WeatherForecastInformation weatherForecastInformation;
//            if (data.containsKey(city)) {
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
//                weatherForecastInformation = data.get(city);
//            } else {
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
//                HttpClient httpClient = new DefaultHttpClient();
//                String pageSourceCode = "";
//
//                // make the HTTP request to the web service
//                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS);
//                HttpResponse httpGetResponse = httpClient.execute(httpGet);
//                HttpEntity httpGetEntity = httpGetResponse.getEntity();
//                if (httpGetEntity != null) {
//                    pageSourceCode = EntityUtils.toString(httpGetEntity);
//                }
//                if (pageSourceCode == null) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
//                    return;
//                } else Log.i(Constants.TAG, pageSourceCode);
//
//                // Parse the page source code into a JSONObject and extract the needed information
//                JSONObject content = new JSONObject(pageSourceCode);
//                JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//                JSONObject weather;
//                StringBuilder condition = new StringBuilder();
//                for (int i = 0; i < weatherArray.length(); i++) {
//                    weather = weatherArray.getJSONObject(i);
//                    condition.append(weather.getString(Constants.MAIN)).append(" : ").append(weather.getString(Constants.DESCRIPTION));
//
//                    if (i < weatherArray.length() - 1) {
//                        condition.append(";");
//                    }
//                }
//                JSONObject main = content.getJSONObject(Constants.MAIN);
//                String temperature = main.getString(Constants.TEMP);
//                String pressure = main.getString(Constants.PRESSURE);
//                String humidity = main.getString(Constants.HUMIDITY);
//                JSONObject wind = content.getJSONObject(Constants.WIND);
//                String windSpeed = wind.getString(Constants.SPEED);
//
//                // Create a WeatherForecastInformation object with the information extracted from the JSONObject
//                weatherForecastInformation = new WeatherForecastInformation(temperature, windSpeed, condition.toString(), pressure, humidity);
//
//                // Cache the information for the given city
//                serverThread.setData(city, weatherForecastInformation);
//            }

//            if (weatherForecastInformation == null) {
//                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
//                return;
//            }

        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}