package com.example.familymapclient2.models;

public class DataTask {

    String authToken;
    String ServerHost;
    String ServerPort;

    public DataTask(String authToken, String serverHost, String serverProxy) {
        this.authToken = authToken;
        ServerHost = serverHost;
        ServerPort = serverProxy;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getServerHost() {
        return ServerHost;
    }

    public String getServerPort() {
        return ServerPort;
    }
}
