package com.masta.patch.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpConnection {

    private HttpURLConnection connection;

    private void openConnection(final String urlPath) {
        URL url = null;
        try {
            url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readResponse(final String urlPath) {
        openConnection(urlPath);
        StringBuffer content = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

}
