package pro3.database.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
//beans for the bunnies' many queues
  public static final String EXCHANGE_NAME = "slaughterhouse.exchange";
  public static final String QUEUE_NAME = "q.animal-registration";
  public static final String ROUTING_KEY = "animal.registration.key";
  public static final String SUCCESS_QUEUE_NAME = "q.animal-registration-success";
  public static final String SUCCESS_ROUTING_KEY = "animal.registration.success";

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue animalRegistrationQueue() {
    return new Queue(QUEUE_NAME);
  }

  @Bean
  public Binding binding(Queue animalRegistrationQueue, TopicExchange exchange) {
    return BindingBuilder.bind(animalRegistrationQueue)
        .to(exchange)
        .with(ROUTING_KEY);
  }
  @Bean
  public Queue animalRegistrationSuccessQueue() {
    return new Queue(SUCCESS_QUEUE_NAME);
  }

  @Bean
  public Binding successBinding(Queue animalRegistrationSuccessQueue, TopicExchange exchange) {
    return BindingBuilder.bind(animalRegistrationSuccessQueue)
        .to(exchange)
        .with(SUCCESS_ROUTING_KEY);
  }
}