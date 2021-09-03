package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Stub
public class PostRepository {
    private Map<Long, Post> posts;

    public PostRepository() {
        posts = new ConcurrentHashMap<>();
        Post post1 = new Post("1");
        posts.put(post1.getId(), post1);
        Post post2 = new Post("22");
        posts.put(post2.getId(), post2);
        Post post3 = new Post("333");
        posts.put(post3.getId(), post3);
    }


    public Map<Long, Post> all() {
        Map<Long, Post> allPosts = new ConcurrentHashMap<>();
        allPosts.putAll(posts);
        return allPosts;
    }

    public Optional<Post> getById(long id) {
        return Optional.of(posts.get(id));
    }

    public Optional<Post> save(Post post) {

        Optional optionalPost;
        if (post.getId() == 0) {
            Post savePost = new Post(post.getContent());
            posts.put(savePost.getId(), savePost);
            optionalPost = Optional.of(savePost);
        } else if (posts.containsKey(post.getId())) {
            Post savePost = post;
            posts.replace(savePost.getId(), savePost);
            optionalPost = Optional.of(savePost);
        } else optionalPost = null;
        return optionalPost;
    }

    public void removeById(long id) {
        posts.remove(id);
    }
}
