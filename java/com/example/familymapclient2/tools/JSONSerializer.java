package com.example.familymapclient2.tools;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public class JSONSerializer {
    public <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }


    public String generate(Object ob) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(ob);

        return jsonString;

    }
}