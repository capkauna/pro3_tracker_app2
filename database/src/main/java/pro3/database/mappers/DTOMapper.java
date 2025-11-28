package pro3.database.mappers;

import pro3.database.entities.*;
import pro3.shared_dtos.dtos.Product.HalfAnimalProductInfoResponseDTO;
import pro3.shared_dtos.dtos.Product.ProductInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;
import pro3.shared_dtos.dtos.Part.PartCreationRequestDTO;
import pro3.shared_dtos.dtos.Part.PartInfoResponseDTO;
import pro3.shared_dtos.dtos.Product.ProductInfoResponseDTO;
import pro3.shared_dtos.dtos.Product.SameTypeProductInfoResponseDTO;
import pro3.shared_dtos.dtos.Tray.TrayCreationRequestDTO;
import pro3.shared_dtos.dtos.Tray.TrayInfoResponseDTO;

import java.io.InvalidObjectException;
import java.util.List;
import java.util.stream.Collectors;

// Dedicated class for mapping between the DTOs and the origin entities
public class DTOMapper {

  // -------------------------------------------------
  // ANIMAL MAPPING
  // -------------------------------------------------

  public static AnimalInfoResponseDTO animalToDTO(Animal animal) {
    if (animal == null) {
      return null;
    }
    return new AnimalInfoResponseDTO(
        animal.getId(),
        animal.getWeight(),
        animal.getRegNo(),
        animal.getRegistrationDate(),
        animal.getOrigin(),
        animal.isButchered()
    );
  }

  public static List<AnimalInfoResponseDTO> animalToDTOList(List<Animal> animals) {
    return animals.stream()
        .map(DTOMapper::animalToDTO)
        .collect(Collectors.toList());
  }

  public static Animal animalRegToEntity(AnimalRegistrationRequestDTO request) {
    if (request == null) {
      return null;
    }

    return new Animal(
        request.getWeight(),
        request.getRegNo(),
        request.getDate(),
        request.getOrigin()
    );
  }

  // -------------------------------------------------
  // PART MAPPING
  // -------------------------------------------------
   public static Part partCreationToEntity(PartCreationRequestDTO request)
   {
     if (request == null)
     {
       throw new IllegalArgumentException("PartCreationRequestDTO is null");
     }

     return new Part(
         request.getType(),
         request.getAnimalId(),
         request.getWeight()
     );
   }

  public static PartInfoResponseDTO partToDTO(Part part)
  {
    if (part == null)
    {
      throw new IllegalArgumentException("Part is null");
    }
    return new PartInfoResponseDTO(
        part.getId(),
        part.getType(),
        part.getAnimalId(),
        part.getWeight(),
        part.getTrayId(),
        part.isPackaged(),
        part.getProductId()
    );
  }

  public static List<PartInfoResponseDTO> partToDTOList(List<Part> parts)
  {
    return parts.stream()
        .map(DTOMapper::partToDTO)
        .collect(Collectors.toList());
  }


  // -------------------------------------------------
  // TRAY MAPPING
  // -------------------------------------------------

  public static TrayInfoResponseDTO trayToDTO(Tray tray) {
    if (tray == null) {
      return null;
    }

    return new TrayInfoResponseDTO(
        tray.getId(),
        tray.getMaxWeightCapacity(),
        tray.getCurrentWeight(),
        tray.getType(),
        tray.isPackaged(),
        tray.isFull()
    );
  }

  public static List<TrayInfoResponseDTO> trayToDTOList(List<Tray> trays) {
    return trays.stream()
        .map(DTOMapper::trayToDTO)
        .collect(Collectors.toList());
  }

  public static Tray trayCreationToEntity(TrayCreationRequestDTO request) {
      if (request == null) {
          return null;
      }

      // Tray constructor: Tray(PartType type, double maxWeightCapacity)
      return new Tray(
              request.getType(),
              request.getMaxWeightCapacity()
      );
  }

    // -------------------------------------------------
    // PRODUCT MAPPING
    // -------------------------------------------------

public static ProductInfoResponseDTO productToDTO (Product product) throws InvalidObjectException {
          if (product == null) {
              return null;
          }

          // Map nested parts (if the service has populated them)
          List<PartInfoResponseDTO> partDTOs = null;
          if (product instanceof SameTypeProduct) {
              SameTypeProduct sameTypeProduct = (SameTypeProduct) product;
              partDTOs = partToDTOList(sameTypeProduct.getParts());

              return new SameTypeProductInfoResponseDTO(
                      sameTypeProduct.getId(),
                      sameTypeProduct.getType(),
                      partDTOs
              );
          } else if (product instanceof HalfAnimalProduct) {
              HalfAnimalProduct halfAnimalProduct = (HalfAnimalProduct) product;
              partDTOs = partToDTOList(halfAnimalProduct.getParts());

              return new HalfAnimalProductInfoResponseDTO(
                      halfAnimalProduct.getId(),
                      partDTOs
              );
          } else {
              // Unknown subclass – if you ever add more, handle them here.
              throw new InvalidObjectException(
                      "Unknown Product subclass: " + product.getClass().getName()
              );
          }
      }

      public static List<ProductInfoResponseDTO> productToDTOList(List<Product> products)
      throws InvalidObjectException {

          return products.stream()
                  .map(product -> {
                      try {
                          return productToDTO(product);
                      } catch (InvalidObjectException e) {
                          // Wrap checked exception into a runtime one for use in streams
                          throw new RuntimeException(e);
                      }
                  })
                  .collect(Collectors.toList());
      }
  }




