package pro3.database.grpc;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import pro3.database.services.AnimalService;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.grpc.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@GrpcService
public class AnimalServiceImpl extends AnimalServiceGrpc.AnimalServiceImplBase {

  private final AnimalService animalService;

  @Autowired
  public AnimalServiceImpl(AnimalService animalService) {
    this.animalService = animalService;
  }

  // ----------------------------------------------------
  // R1: AddAnimal
  // ----------------------------------------------------
  @Override
  public void addAnimal(AnimalRegistrationRequest protoRequest,
      StreamObserver<AnimalInfoResponse> responseObserver) {

    try {
      // 1. Convert from Protobuf Message -> DTO
      AnimalRegistrationRequestDTO requestDTO = protoToDto(protoRequest);

      // 2. Call your existing business logic service
      AnimalInfoResponseDTO responseDTO = animalService.createAnimal(requestDTO);

      // 3. Convert from DTO -> Protobuf Message
      AnimalInfoResponse protoResponse = dtoToProto(responseDTO);

      // 4. Send the gRPC response
      responseObserver.onNext(protoResponse);
      responseObserver.onCompleted();

    } catch (DateTimeParseException e) {
      // Handle the case where the client sent a bad date string
      responseObserver.onError(
          io.grpc.Status.INVALID_ARGUMENT
              .withDescription("Invalid date format. Please use YYYY-MM-DD.")
              .asRuntimeException()
      );
    } catch (Exception e) {
      // Handle other potential errors (like database constraints)
      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .asRuntimeException()
      );
    }
  }

  // ----------------------------------------------------
  // R2: GetAnimalByRegNo
  // ----------------------------------------------------
  @Override
  public void getAnimalByRegNo(
      GetAnimalByRegNoRequest request,
      StreamObserver<AnimalInfoResponse> responseObserver
  ) {
    try {
      String regNo = request.getRegNo();

      Optional<AnimalInfoResponseDTO> optional = animalService.getAnimalByRegNo(regNo);

      if (optional.isEmpty()) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Animal with regNo " + regNo + " not found")
                .asRuntimeException()
        );
        return;
      }

      AnimalInfoResponseDTO dto = optional.get();
      AnimalInfoResponse proto = dtoToProto(dto);

      responseObserver.onNext(proto);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to get animal by regNo: " + e.getMessage())
              .asRuntimeException()
      );
    }
  }

  // ----------------------------------------------------
  // R3: GetAnimalsByOrigin
  // ----------------------------------------------------
  @Override
  public void getAnimalsByOrigin(
      GetAnimalsByOriginRequest request,
      StreamObserver<AnimalListResponse> responseObserver
  ) {
    try {
      String origin = request.getOrigin();

      List<AnimalInfoResponseDTO> dtos = animalService.getAnimalsByOrigin(origin);

      AnimalListResponse.Builder listBuilder = AnimalListResponse.newBuilder();
      for (AnimalInfoResponseDTO dto : dtos) {
        listBuilder.addAnimals(dtoToProto(dto));
      }

      responseObserver.onNext(listBuilder.build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to get animals by origin: " + e.getMessage())
              .asRuntimeException()
      );
    }
  }

  // ----------------------------------------------------
  // R4: GetAnimalsByDate
  // ----------------------------------------------------
  @Override
  public void getAnimalsByDate(
      GetAnimalsByDateRequest request,
      StreamObserver<AnimalListResponse> responseObserver
  ) {
    try {
      LocalDate date = protoDateToLocalDate(request.getDate());

      List<AnimalInfoResponseDTO> dtos = animalService.getAnimalsByDate(date);

      AnimalListResponse.Builder listBuilder = AnimalListResponse.newBuilder();
      for (AnimalInfoResponseDTO dto : dtos) {
        listBuilder.addAnimals(dtoToProto(dto));
      }

      responseObserver.onNext(listBuilder.build());
      responseObserver.onCompleted();
     } catch (DateTimeParseException e) {
      responseObserver.onError(
          io.grpc.Status.INVALID_ARGUMENT
              .withDescription("Invalid date format. Please use YYYY-MM-DD.")
              .asRuntimeException()
      );
    }
    catch (Exception e) {
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to get animals by date: " + e.getMessage())
              .asRuntimeException()
      );
    }
  }

  // ----------------------------------------------------
  // R5: MarkAnimalAsButchered
  // ----------------------------------------------------
  @Override
  public void markAnimalAsButchered(
      MarkAnimalButchered request,
      StreamObserver<AnimalInfoResponse> responseObserver
  ) {
    int animalId = request.getAnimalId();

    try {
      // Service returns Optional<AnimalInfoResponseDTO>
      Optional<AnimalInfoResponseDTO> optional = animalService.markAnimalAsButchered(animalId);

      if (optional.isEmpty()) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Animal with id " + animalId + " not found")
                .asRuntimeException()
        );
        return;
      }

      AnimalInfoResponseDTO dto = optional.get();
      AnimalInfoResponse proto = dtoToProto(dto);

      responseObserver.onNext(proto);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to mark animal as butchered: " + e.getMessage())
              .asRuntimeException()
      );
    }
  }

  // ----------------------------------------------------
  // Mapping helpers
  // ----------------------------------------------------

  private AnimalRegistrationRequestDTO protoToDto(AnimalRegistrationRequest proto) {
    AnimalRegistrationRequestDTO dto = new AnimalRegistrationRequestDTO();
    dto.setWeight(proto.getWeight());
    dto.setRegNo(proto.getRegNo());
    dto.setOrigin(proto.getOrigin());
    dto.setDate(LocalDate.parse(proto.getDate()));
    return dto;
  }

  private AnimalInfoResponse dtoToProto(AnimalInfoResponseDTO dto) {

    String isoDate = dto.getRegistrationDate().toString();

    return AnimalInfoResponse.newBuilder()
        .setAnimalId(dto.getAnimalid())
        .setWeight(dto.getWeight())
        .setRegNo(dto.getRegNo())
        .setOrigin(dto.getOrigin())
        .setRegistrationDate(isoDate)
        .setIsButchered(dto.isButchered())
        .build();
  }

  private LocalDate protoDateToLocalDate(String protoDate) {
    return LocalDate.parse(protoDate);
  }


}
