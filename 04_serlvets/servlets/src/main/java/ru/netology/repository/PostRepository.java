package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final AtomicLong id = new AtomicLong(0);
    private final ConcurrentHashMap<AtomicLong, Post> posts = new ConcurrentHashMap<>();

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        AtomicLong tempAtomic = new AtomicLong(id);
        return Optional.ofNullable(posts.get(tempAtomic));
    }

    public Post save(Post post) {

        long postID = post.getId();

        if (postID == 0) {
            id.getAndIncrement();
            posts.put(id, post);
        }

        if (postID != 0) {
            AtomicLong tempAtomic = new AtomicLong(postID);
            if (posts.containsKey(tempAtomic)) {
                noSuchPostError(postID);
            }
            posts.replace(tempAtomic, post);
        }
        return post;
    }

    public void removeById(long id) {
        posts.remove(new AtomicLong(id));
    }

    public void noSuchPostError(long id) {
        String msg = "Post with ID {" + id + "} not found";
        throw new NotFoundException(msg);
    }
}
