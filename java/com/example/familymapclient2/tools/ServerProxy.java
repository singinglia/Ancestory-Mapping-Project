package com.example.familymapclient2.tools;

import com.example.familymapclient2.tools.JSONSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.AllEventResult;
import results.AllPersonResult;
import results.LoginResult;
import results.RegisterResult;

public class ServerProxy {

    private String serverHost;
    private String serverPort;
    private JSONSerializer serializer;

    public ServerProxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        serializer = new JSONSerializer();
    }

    public RegisterResult register(RegisterRequest request){

        try {
            String JSONRequest = serializer.generate(request);

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            String JSONResult = PostRequest(JSONRequest, url);

            return serializer.deserialize(JSONResult, RegisterResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult(false, "ERROR: Request or JSON Error");
        }


    }

    public LoginResult login(LoginRequest request){
        try {
            String JSONRequest = serializer.generate(request);

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            String JSONResult = PostRequest(JSONRequest, url);

            return serializer.deserialize(JSONResult, LoginResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult(false, "ERROR: Request or JSON Error");
        }
    }

    public AllPersonResult getPeople(String authToken){
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            String JSONResult = GetRequest(url, authToken);

            return serializer.deserialize(JSONResult, AllPersonResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return new AllPersonResult(false, "ERROR: Request or JSON Error");
        }
    }

    public AllEventResult getEvents(String authToken){
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            String JSONResult = GetRequest(url, authToken);

            return serializer.deserialize(JSONResult, AllEventResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return new AllEventResult(false, "ERROR: Request or JSON Error");
        }
    }

    private String PostRequest(String JSONString, URL url) throws IOException {
        try {

            //Set up Connection
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();


            //Send Body
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString(JSONString, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                return readString(respBody);
            } else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream respBody = http.getErrorStream();
                return readString(respBody);
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
            }


        } catch (IOException e){
            e.printStackTrace();
            throw new IOException();
        }
        return null;
    }

    private String GetRequest(URL url, String authToken) throws IOException {

        try {

            //Set up Connection
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                return readString(respBody);
            }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream respBody = http.getErrorStream();
                return readString(respBody);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
            }


        } catch (IOException e){
            e.printStackTrace();
            throw new IOException();
        }
        return null;

    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}
