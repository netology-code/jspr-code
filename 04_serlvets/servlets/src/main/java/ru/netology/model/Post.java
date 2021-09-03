package ru.netology.model;

public class Post {
    private static long count = 0;
    private long id;
    private String content;

    public Post(String content) {
        this.id = ++count;
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
