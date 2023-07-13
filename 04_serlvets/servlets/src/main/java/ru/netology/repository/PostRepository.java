package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {
  private final AtomicLong idPost = new AtomicLong();
  private final Map<Long, Post> postMap = new ConcurrentHashMap<>();
  public List<Post> all() {
    return new ArrayList<>(postMap.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(postMap.get(id));
  }

  public Post save(Post post) {
    if (post.getId()!=0){
      if (postMap.containsKey(post.getId())) {
        postMap.remove(post.getId());
        postMap.put(post.getId(), post);
      } else {
        throw new NotFoundException();
      }
    }
    if (post.getId() == 0){
      var id = idPost.incrementAndGet();
      post.setId(id);
    }
    postMap.put(post.getId(), post);
    return post;
  }

  public void removeById(long id) {
    if (!postMap.containsKey(id)) {
      throw new NotFoundException();
    }
    postMap.remove(id);
  }
}
