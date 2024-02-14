package ru.netology.repository;

import ru.netology.model.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepositoryImpl extends PostRepository {
    private final List<Post> posts = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Post> all() {
        return posts;
    }

    @Override
    public Optional<Post> getById(long id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst();
    }

    @Override
    public synchronized Post save(Post post) {
        if (post.getId() == 0) {
            long postId = nextId.getAndIncrement();
            post.setId(postId);
            posts.add(post);
            return post;
        } else {
            for (int i = 0; i < posts.size(); i++) {
                if (posts.get(i).getId() == post.getId()) {
                    posts.set(i, post);
                    return post;
                }
            }
            throw new IllegalArgumentException("Post with id " + post.getId() + " not found");
        }
    }

    @Override
    public synchronized void removeById(long id) {
        posts.removeIf(post -> post.getId() == id);
    }
}
