package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PostRepository {
    private List<Post> posts = new ArrayList<>();
    private int count;

    public synchronized List<Post> all() {
        return posts;
    }

    public synchronized Optional<Post> getById(long id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst();
    }

    public synchronized Post save(Post post) {
        if (post.getId() == 0) {
            Post newPost = new Post(count++, post.getContent());
            posts.add(newPost);
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

    public synchronized void removeById(long id) {
        Iterator<Post> iterator = posts.iterator();
        while (iterator.hasNext()) {
            Post post = iterator.next();
            if (post.getId() == id) {
                iterator.remove();
                return;
            }
        }
        throw new NotFoundException("Post with id " + id + " not found");
    }
}
