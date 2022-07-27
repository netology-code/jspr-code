package ru.netology.repository;

import org.apache.http.client.utils.URLEncodedUtils;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Stub
public class PostRepository {

    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger postsCounter = new AtomicInteger(0);

    public List<Post> all() {
        return (List<Post>) posts.values();
    }

    public Optional<Post> getById(long id) {
        return posts.values().stream()
                .filter(x -> x.getId() == id)
                .findAny();
    }

    public Post save(Post post) {
        if (post.getId() != 0) {

            var postInList = getById(post.getId());

            if (postInList.isPresent()) {
                posts.put(postInList.get().getId(), post);
            } else {
                throw new NotFoundException("Невозможно сохранить пост!");
            }
        } else {
            post.setId(postsCounter.addAndGet(1));
            posts.put(post.getId(), post);
        }
        post.setContent(String.valueOf(URLEncodedUtils.parse(post.getContent(),
                StandardCharsets.UTF_8).get(0)));
        return post;
    }

    public void removeById(long id) {
        posts.values().removeIf(post -> post.getId() == id);
    }
}
