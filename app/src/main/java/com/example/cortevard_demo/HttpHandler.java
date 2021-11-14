package com.example.cortevard_demo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {
    private static final String TAG = com.example.cortevard_demo.HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String makeServiceCall(String reqMethod, JSONArray params) {
        String response = null;
        try {
            URL url = new URL("https://www.cortevasemillasconosur.com/rd/rd_api_rest.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("accion", reqMethod);
            jsonParam.put("user", "MobileApp");
            jsonParam.put("pwd", "12345");
            // adding post params
            if (params != null) {
                jsonParam.put("datos",params);
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            bw.write(jsonParam.toString());
            bw.flush();
            bw.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
            conn.disconnect();
        } catch (MalformedURLException e) {
            //Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            //Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            //Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
