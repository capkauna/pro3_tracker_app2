package pro3.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro3.database.entities.Animal;
import pro3.database.mappers.DTOMapper;
import pro3.database.repository.AnimalRepository;
import pro3.shared_dtos.dtos.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.AnimalRegistrationRequestDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Animal-related business logic.
 * This class acts as the "bridge" between the gRPC/API layer
 * and the database (Repository) layer.
 */
@Service // This annotation tells Spring to manage this class as a Service
public class AnimalService
{

  private final AnimalRepository animalRepository;

  // We use constructor injection - it's a best practice for required dependencies.
  @Autowired
  public AnimalService(AnimalRepository animalRepository)
  {
    this.animalRepository = animalRepository;
  }

  /**
   * Registers a new animal in the system.
   *
   * @param requestDTO The DTO containing the new animal's data.
   * @return An DTO with the newly created animal's info, including its generated ID.
   */
  @Transactional // Ensures the save operation is atomic (all or nothing)
  public AnimalInfoResponseDTO createAnimal(
      AnimalRegistrationRequestDTO requestDTO)
  {
    // 1. Use your mapper to convert the DTO to an entity
    //    (We don't need DTOMapper. here because its methods are static)
    Animal newAnimal = DTOMapper.animalRegToEntity(requestDTO);

    // 2. Use the repository to save the new entity
    Animal savedAnimal = animalRepository.save(newAnimal);

    // 3. Use your mapper to convert the saved entity back to a response DTO
    return DTOMapper.animalToDTO(savedAnimal);
  }

  /**
   * Finds a single animal by its registration number.
   *
   * @param regNo The registration number to search for.
   * @return An Optional containing the animal's DTO if found, or empty if not.
   */
  public Optional<AnimalInfoResponseDTO> getAnimalByRegNo(String regNo)
  {
    // 1. Find the entity
    Optional<Animal> animalOptional = animalRepository.findByRegNo(regNo);

    // 2. Map the entity to a DTO (if it exists)
    return animalOptional.map(DTOMapper::animalToDTO);
  }

  /**
   * Finds all animals from a specific origin.
   *
   * @param origin The origin to search for.
   * @return A list of animal DTOs.
   */
  public List<AnimalInfoResponseDTO> getAnimalsByOrigin(String origin)
  {
    List<Animal> animals = animalRepository.findByOrigin(origin);
    return DTOMapper.animalToDTOList(animals);
  }

  /**
   * Finds all animals registered on a specific date.
   *
   * @param date The date to search for.
   * @return A list of animal DTOs.
   */
  public List<AnimalInfoResponseDTO> getAnimalsByDate(LocalDate date)
  {
    List<Animal> animals = animalRepository.findByRegistrationDate(date);
    return DTOMapper.animalToDTOList(animals);
  }

  public AnimalInfoResponseDTO markAnimalAsButchered(int animalId) {
    Animal animaltoUpdate = animalRepository.findById(animalId).get();
    animaltoUpdate.setButchered(true);
    animalRepository.save(animaltoUpdate);
    return DTOMapper.animalToDTO(animaltoUpdate);
  }
}