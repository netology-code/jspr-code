package ru.netology.repository;

import ru.netology.model.Post;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class PostRepositoryImpl extends PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            long postId = nextId.getAndIncrement();
            post.setId(postId);
            posts.put(postId, post);
            return post;
        } else {
            if (posts.replace(post.getId(), post) == null) {
                throw new IllegalArgumentException("Post with id " + post.getId() + " not found");
            }
            return post;
        }
    }

    @Override
    public void removeById(long id) {
        if (posts.remove(id) == null) {
            throw new IllegalArgumentException("Post with id " + id + " not found");
        }
    }

    @Override
    public Optional<Post> getById(long id) {
        Post post = posts.get(id);
        return Optional.ofNullable(post);
    }
}