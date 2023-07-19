package ru.netology.repository;


import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final Map<Long, Post> gagDataBases = new ConcurrentHashMap<>();
    private final Map<Long, Post> gagRemoveDataBases = new ConcurrentHashMap<>();
    AtomicLong id = new AtomicLong();

    @Override
    public List<Post> all() {
        return new ArrayList<>(gagDataBases.values());
    }

    @Override
    public Optional<Post> getById(long id) {
        return Optional.ofNullable(gagDataBases.get(id));
    }

    @Override
    public Post save(Post post) {
        if (post.id() != 0) {
            addAndRemovePost(post.id());
            gagDataBases.put(post.id(), post);
        }
        if (post.id() == 0) {
            final var idPost = id.incrementAndGet();
            Post newPost = new Post(idPost, post.content());
            gagDataBases.put(idPost, newPost);
            return newPost;
        }
        return post;
    }

    @Override
    public void removePostById(long id) {
        addAndRemovePost(id);
    }

    private void addAndRemovePost(long id) {
        if (gagDataBases.containsKey(id)) {
            gagRemoveDataBases.put(gagDataBases.get(id).id(), gagDataBases.get(id));
            gagDataBases.remove(id);
        } else {
            notFoundException();
        }
    }

    private void notFoundException() {
        throw new NotFoundException();
    }
}