package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository {
    private final List<Post> posts = new ArrayList<>();
    private long nextPostId = 1L;

    public synchronized List<Post> all() {
        return posts;
    }

    public synchronized Optional<Post> getById(long id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst();
    }

    public synchronized Post save(Post post) {
        if (post.getId() == 0L) {
            post.setId(nextPostId++);
            posts.add(post);
            return post;
        } else {
            final Post oldPost = getById(post.getId()).orElseThrow(NotFoundException::new);
            oldPost.setContent(post.getContent());
            return oldPost;
        }
    }

    public synchronized void removeById(long id) {
        posts.removeIf(post -> post.getId() == id);
    }
}
