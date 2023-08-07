package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PostRepository {
    private ConcurrentMap<Long, Post> storage = new ConcurrentHashMap<>();
    private AtomicInteger count = new AtomicInteger();

    public Collection<Post> all() {
        return storage.values();
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            Post newPost = new Post(count.incrementAndGet(), post.getContent());
            storage.put(newPost.getId(), newPost);
            return newPost;
        } else {
            Optional<Post> foundedPost = getById(post.getId());
            if (foundedPost.isPresent()) {
                foundedPost.get().setContent(post.getContent());
                return foundedPost.get();
            } else {
                throw new NotFoundException("Post with id " + post.getId() + " not found");
            }
        }
    }

    public void removeById(long id) {
        if (storage.remove(id) == null) {
            throw new NotFoundException("Post with id " + id + " not found");
        }
    }
}
