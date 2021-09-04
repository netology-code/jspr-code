package ru.netology.model;

import java.util.concurrent.atomic.AtomicLong;

public class Post {

    private Long id;
    private String content;

    public Post(Long id, String content) {
        this.id = id;
        this.content = content;

    }

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
