package ru.netology.repository;


import ru.netology.model.Post;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class PostRepository {
    private static AtomicLong postId = new AtomicLong();

    private Map<Long, Post> posts;


    public PostRepository() {
        posts = new ConcurrentHashMap<>();
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
            Post savePost = new Post(postId.incrementAndGet(), post.getContent());
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
