package pro3.database.mappers;




import pro3.database.entities.Animal;
import pro3.shared_dtos.dtos.*;

import java.util.List;
import java.util.stream.Collectors;

//Single Responsibility Principle
//Dedicated class for mapping between the DTOs and the origin entity
public class DTOMapper
{

  public static AnimalInfoResponseDTO animalToDTO(Animal animal)
  {
    if (animal == null)
    {
      return null;
    }
    return new AnimalInfoResponseDTO(animal.getId(), animal.getWeight(),
        animal.getRegNo(), animal.getRegistrationDate(), animal.getOrigin(),
        animal.isButchered());
  }

  public static List<AnimalInfoResponseDTO> animalToDTOList(List<Animal> animals)
  {
    return animals.stream().map(DTOMapper::animalToDTO)
        .collect(Collectors.toList());
  }

  //
  public static Animal animalRegToEntity(AnimalRegistrationRequestDTO request)
  {
    if (request == null)
    {
      return null;
    }

    return new Animal(request.getWeight(), request.getRegNo(),
        request.getDate(), request.getOrigin());
  }
}
