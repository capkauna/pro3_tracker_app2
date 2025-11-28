package pro3.database.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // Import this
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro3.database.mappers.DTOMapper;
import pro3.database.services.AnimalService;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;

@Component
public class AnimalMessageListener {

  private final AnimalService animalService;
  private final RabbitTemplate rabbitTemplate; // So we can send a reply
  private static final Logger log = LoggerFactory.getLogger(AnimalMessageListener.class);

  // Inject RabbitTemplate
  @Autowired
  public AnimalMessageListener(AnimalService animalService, RabbitTemplate rabbitTemplate) {
    this.animalService = animalService;
    this.rabbitTemplate = rabbitTemplate;
  }

  // This listens for the *request* from the service_tier
  @RabbitListener(queues = "q.animal-registration")
  public void onAnimalRegistrationRequest(AnimalRegistrationRequestDTO request) {
    log.info("Received request for new animal: " + request.getRegNo());
    try {

      AnimalInfoResponseDTO savedAnimalDTO = animalService.createAnimal(request);

      // Publish the "success" event
      // This tells other services (like Butchering) that the animal exists.
      log.info("Publishing success event for animal ID: " + savedAnimalDTO.getAnimalid());
      rabbitTemplate.convertAndSend(
          "slaughterhouse.exchange",        // Use the same exchange
          "animal.registration.success",    // A *new* routing key
          savedAnimalDTO                    // Send the full DTO
      );

    } catch (Exception e) {
      log.error("Failed to process animal from queue: " + e.getMessage());
      // TODO: handle failure message sending later

    }
  }
}