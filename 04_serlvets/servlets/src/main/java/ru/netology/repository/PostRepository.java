package ru.netology.repository;

import ru.netology.model.Post;

import java.util.Collection;
import java.util.Optional;

public interface PostRepository {

    public Collection<Post> all();

    public Optional<Post> getById(long id);

    public Post save(Post post);

    public void removeById(long id);
}
