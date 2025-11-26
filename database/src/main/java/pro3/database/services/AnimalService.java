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

    @Transactional
    public AnimalInfoResponseDTO createAnimal(AnimalRegistrationRequestDTO requestDTO) {
        Animal newAnimal = DTOMapper.animalRegToEntity(requestDTO);
        Animal savedAnimal = animalRepository.save(newAnimal);
        return DTOMapper.animalToDTO(savedAnimal);
    }

    public Optional<AnimalInfoResponseDTO> getAnimalByRegNo(String regNo) {
        return animalRepository.findByRegNo(regNo)
                .map(DTOMapper::animalToDTO);
    }

    public List<AnimalInfoResponseDTO> getAnimalsByOrigin(String origin) {
        List<Animal> animals = animalRepository.findByOrigin(origin);
        return DTOMapper.animalToDTOList(animals);
    }

    public List<AnimalInfoResponseDTO> getAnimalsByDate(LocalDate date) {
        List<Animal> animals = animalRepository.findByRegistrationDate(date);
        return DTOMapper.animalToDTOList(animals);
    }

  public AnimalInfoResponseDTO getAnimalById(int id)
  {
    return DTOMapper.animalToDTO(animalRepository.findById(id).orElse(null));
  }

    @Transactional
    public Optional<AnimalInfoResponseDTO> markAnimalAsButchered(int animalId) {
        return animalRepository.findById(animalId)
                .map(animal -> {
                    animal.setButchered(true);
                    Animal updated = animalRepository.save(animal);
                    return DTOMapper.animalToDTO(updated);
                });
    }
}
