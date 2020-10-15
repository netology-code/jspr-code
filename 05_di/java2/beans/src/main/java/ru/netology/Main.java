package ru.netology;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.service.PostService;

public class Main {
  public static void main(String[] args) {
    // отдаём класс конфигурации
    final var context = new AnnotationConfigApplicationContext(JavaConfig.class);

    // получаем по имени бина
    final var controller = context.getBean("postController");

    // получаем по классу бина
    final var service = context.getBean(PostService.class);

    // по умолчанию создаётся лишь один объект на BeanDefinition
    final var isSame = service == context.getBean("postService");
  }
}
