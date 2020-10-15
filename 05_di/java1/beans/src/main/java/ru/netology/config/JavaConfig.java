package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryStubImpl;
import ru.netology.service.PostService;

@Configuration
public class JavaConfig {
  @Bean
  // аргумент метода и есть DI
  // название метода - название бина
  public PostController postController(PostService service) {
    return new PostController(service);
  }

  @Bean
  public PostService postService(PostRepository repository) {
    return new PostService(repository);
  }

  @Bean
  public PostRepository postRepository() {
    return new PostRepositoryStubImpl();
  }
}
