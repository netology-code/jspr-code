package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Stub
public class PostRepository {

    private final List<Post> posts = new CopyOnWriteArrayList<>();
    private int postsCounter = 0;

    public List<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        return posts.stream()
                .filter(x -> x.getId() == id)
                .findAny();
    }

    public Post save(Post post) {
        if (post.getId() != 0) {

            var postInList = getById(post.getId());

            if (postInList.isPresent()) {
                posts.set(posts.indexOf(postInList.get()), post);
                return post;
            } else {
                throw new NotFoundException("Невозможно сохранить пост!");
            }
        } else {
            post.setId(postsCounter++);
            posts.add(post);
        }
        return post;
    }

    public void removeById(long id) {
        posts.removeIf(post -> post.getId() == id);
    }
}
