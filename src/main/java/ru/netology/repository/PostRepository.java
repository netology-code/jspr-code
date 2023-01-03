package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong(1);

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Optional<Post> save(Post post) {
        long postId = post.getId();
        if (postId == 0) {
            long newPostId = idCounter.getAndIncrement();
            post.setId(newPostId);
            posts.put(newPostId, post);
            return Optional.of(posts.get(newPostId));
        } else {
            if (posts.containsKey(postId)) posts.put(postId, post);
            return Optional.ofNullable(posts.get(postId));
        }
    }

    public Optional<Post> removeById(long id) {
        return Optional.ofNullable(posts.remove(id));
    }
}
