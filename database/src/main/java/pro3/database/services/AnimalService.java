package pro3.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro3.database.entities.Animal;
import pro3.database.mappers.DTOMapper;
import pro3.database.repository.AnimalRepository;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Animal-related business logic.
 * This class acts as the "bridge" between the gRPC/API layer
 * and the database (Repository) layer.
 */
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    @Autowired
    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Registers a new animal in the system.
     *
     * @param requestDTO DTO containing the new animal's data.
     * @return DTO with the newly created animal's info, including its generated ID.
     */
    @Transactional
    public AnimalInfoResponseDTO createAnimal(AnimalRegistrationRequestDTO requestDTO) {
        // Map DTO -> entity
        Animal newAnimal = DTOMapper.animalRegToEntity(requestDTO);

        // Persist
        Animal saved = animalRepository.save(newAnimal);

        // Map entity -> DTO
        return DTOMapper.animalToDTO(saved);
    }

    /**
     * Find a single animal by its registration number.
     */
    public Optional<AnimalInfoResponseDTO> getAnimalByRegNo(String regNo) {
        return animalRepository.findByRegNo(regNo)
                .map(DTOMapper::animalToDTO);
    }

    /**
     * Find all animals from a specific origin.
     */
    public List<AnimalInfoResponseDTO> getAnimalsByOrigin(String origin) {
        return DTOMapper.animalToDTOList(
                animalRepository.findByOrigin(origin)
        );
    }

    /**
     * Find all animals registered on a specific date.
     */
    public List<AnimalInfoResponseDTO> getAnimalsByDate(LocalDate date) {
        return DTOMapper.animalToDTOList(
                animalRepository.findByRegistrationDate(date)
        );
    }

    /**
     * Mark an animal as butchered.
     *
     * @return Optional containing the updated DTO if the animal exists, otherwise Optional.empty().
     */
    @Transactional
    public Optional<AnimalInfoResponseDTO> markAnimalAsButchered(int animalId) {
        return animalRepository.findById(animalId)
                .map(animal -> {
                    animal.setButchered(true);
                    Animal saved = animalRepository.save(animal);
                    return DTOMapper.animalToDTO(saved);
                });
    }
}