package com.example.familymapclient2.models;

import requests.RegisterRequest;

public class RegisterTask {
    RegisterRequest request;
    String serverHost;
    String serverPort;

    public RegisterTask(RegisterRequest request, String serverHost, String serverPort) {
        this.request = request;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public RegisterRequest getRequest() {
        return request;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }
}
