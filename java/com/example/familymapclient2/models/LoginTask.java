package com.example.familymapclient2.models;

import requests.LoginRequest;

public class LoginTask {

    LoginRequest request;
    String ServerHost;
    String ServerPort;

    public LoginTask(LoginRequest request, String serverHost, String serverProxy) {
        this.request = request;
        ServerHost = serverHost;
        ServerPort = serverProxy;
    }

    public LoginRequest getRequest() {
        return request;
    }

    public String getServerHost() {
        return ServerHost;
    }

    public String getServerPort() {
        return ServerPort;
    }
}
