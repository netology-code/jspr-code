package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Stub
public class PostRepository {
    private final List<Post> posts = new ArrayList<>();
    private long postId = 1L;

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
            Post newPost = new Post();
            newPost.setId(postId++);
            newPost.setContent(post.getContent());
            posts.add(newPost);
            return newPost;
        } else {
            Optional<Post> optionalPost = getById(post.getId());
            if (optionalPost.isPresent()){
                Post oldPost = optionalPost.get();
                oldPost.setContent(post.getContent());
                posts.add(oldPost);
                return oldPost;
            }
            return null;
        }
    }

    public synchronized void removeById(long id) {
        posts.removeIf(post -> post.getId() == id);
    }
}
