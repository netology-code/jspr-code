package ru.netology;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import ru.netology.service.PostService;

public class Main {
  public static void main(String[] args) {
    final var factory = new DefaultListableBeanFactory();
    final var reader = new XmlBeanDefinitionReader(factory);
    reader.loadBeanDefinitions("beans.xml");

    // получаем по имени бина
    final var controller = factory.getBean("postController");

    // получаем по классу бина
    final var service = factory.getBean(PostService.class);

    // по умолчанию создаётся лишь один объект на BeanDefinition
    final var isSame = service == factory.getBean("postService");
  }
}
