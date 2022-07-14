package ru.netology.model;

import com.google.gson.JsonParser;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.apache.http.client.utils.URLEncodedUtils;

import java.beans.JavaBean;
import java.nio.charset.StandardCharsets;

public class Post {

    private long id;
    private String content;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
