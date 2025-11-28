package pro3.service_tier.services;

import pro3.service_tier.events.AnimalArrivedEvent;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.grpc.AnimalInfoResponse;
import slaughterhouse.assignment.grpc.AnimalListResponse;
import slaughterhouse.assignment.grpc.AnimalRegistrationRequest;
import slaughterhouse.assignment.grpc.AnimalServiceGrpc;
import slaughterhouse.assignment.grpc.GetAnimalByRegNoRequest;
import slaughterhouse.assignment.grpc.GetAnimalsByDateRequest;
import slaughterhouse.assignment.grpc.GetAnimalsByOriginRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReceptionService {

  private final AnimalServiceGrpc.AnimalServiceBlockingStub animalStub;
  private final ApplicationEventPublisher eventPublisher;

  public ReceptionService(AnimalServiceGrpc.AnimalServiceBlockingStub animalStub,
      ApplicationEventPublisher eventPublisher) {
    this.animalStub = animalStub;
    this.eventPublisher = eventPublisher;
  }

  // ----------------------------------------------------
  // Register animal (uses gRPC AddAnimal)
  // ----------------------------------------------------
  public AnimalInfoResponseDTO registerAnimal(
      AnimalRegistrationRequestDTO request) {
    try {
      // DTO -> gRPC request
      AnimalRegistrationRequest grpcRequest = dtoToGrpcRequest(request);

      // Call remote AnimalService
      AnimalInfoResponse grpcResponse = animalStub.addAnimal(grpcRequest);

      // Publish event for local listeners (butchering etc.)
      AnimalArrivedEvent event = new AnimalArrivedEvent(grpcResponse.getAnimalId());
      eventPublisher.publishEvent(event);

      // gRPC response -> DTO
      return grpcToDto(grpcResponse);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.ALREADY_EXISTS.getCode()
          || e.getStatus().getCode() == Status.INVALID_ARGUMENT.getCode()) {
        throw new IllegalArgumentException(e.getStatus().getDescription());
      }
      throw new RuntimeException("gRPC error while registering animal: " + e.getMessage(), e);
    }
  }

  // ----------------------------------------------------
  // Get single animal by REGISTRATION NUMBER
  // ----------------------------------------------------
  public AnimalInfoResponseDTO findAnimalByRegNo(String regNo) {
    try {
      GetAnimalByRegNoRequest request =
          GetAnimalByRegNoRequest.newBuilder().setRegNo(regNo).build();

      AnimalInfoResponse response = animalStub.getAnimalByRegNo(request);
      return grpcToDto(response);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
        return null; // controller will turn this into 404
      }
      throw new RuntimeException("gRPC error while fetching animal by regNo: " + e.getMessage(), e);
    }
  }

  // ----------------------------------------------------
  // Get animals by origin
  // ----------------------------------------------------
  public List<AnimalInfoResponseDTO> findAnimalsByOrigin(String origin) {
    try {
      GetAnimalsByOriginRequest request =
          GetAnimalsByOriginRequest.newBuilder().setOrigin(origin).build();

      AnimalListResponse response = animalStub.getAnimalsByOrigin(request);
      return grpcListToDtoList(response);
    } catch (StatusRuntimeException e) {
      throw new RuntimeException("gRPC error while fetching animals by origin: " + e.getMessage(), e);
    }
  }

  // ----------------------------------------------------
  // Get animals by date
  // ----------------------------------------------------
  public List<AnimalInfoResponseDTO> findAnimalsByDate(LocalDate date) {
    try {
      GetAnimalsByDateRequest request =
          GetAnimalsByDateRequest.newBuilder().setDate(localDateToGrpcDate(date)).build();

      AnimalListResponse response = animalStub.getAnimalsByDate(request);
      return grpcListToDtoList(response);
    } catch (StatusRuntimeException e) {
      throw new RuntimeException("gRPC error while fetching animals by date: " + e.getMessage(), e);
    }
  }

  // Optional debug helper
  public void show() {
    System.out.println("Reception Service active (using gRPC backend)");
  }

  // ----------------------------------------------------
  // Mapping helpers: DTO <-> gRPC
  // ----------------------------------------------------

  private AnimalRegistrationRequest dtoToGrpcRequest(AnimalRegistrationRequestDTO dto) {
    return AnimalRegistrationRequest.newBuilder()
        .setWeight(dto.getWeight())
        .setRegNo(dto.getRegNo())
        .setOrigin(dto.getOrigin())
        .setDate(localDateToGrpcDate(dto.getDate()))
        .build();
  }

  private AnimalInfoResponseDTO grpcToDto(AnimalInfoResponse grpc) {
    LocalDate registrationDate = LocalDate.parse(grpc.getRegistrationDate());

    return new AnimalInfoResponseDTO(
        grpc.getAnimalId(),
        grpc.getWeight(),
        grpc.getRegNo(),
        registrationDate,
        grpc.getOrigin(),
        grpc.getIsButchered()
    );
  }

  private List<AnimalInfoResponseDTO> grpcListToDtoList(AnimalListResponse listResponse) {
    List<AnimalInfoResponseDTO> dtos = new ArrayList<>();
    for (AnimalInfoResponse grpc : listResponse.getAnimalsList()) {
      dtos.add(grpcToDto(grpc));
    }
    return dtos;
  }

  private String localDateToGrpcDate(LocalDate localDate) {
    return localDate.toString();
  }
}
