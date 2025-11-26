package pro3.database.grpc;

import com.google.type.Date;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

// Your existing service (the "bridge" to the repository)
import pro3.database.services.AnimalService;

// Your DTOs (which the service layer uses)
import pro3.shared_dtos.dtos.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.AnimalRegistrationRequestDTO;

// The NEW classes generated from animal_service.proto
import slaughterhouse.assignment.grpc.AnimalInfoResponse;
import slaughterhouse.assignment.grpc.AnimalRegistrationRequest;
import slaughterhouse.assignment.grpc.AnimalServiceGrpc;

import java.time.LocalDate;

@GrpcService
public class AnimalServiceImpl extends AnimalServiceGrpc.AnimalServiceImplBase {

  private final AnimalService animalService; // Your business logic service

  @Autowired
  public AnimalServiceImpl(AnimalService animalService) {
    this.animalService = animalService;
  }

  /**
   * Implements the AddAnimal RPC from animal_service.proto
   */
  @Override
  public void addAnimal(AnimalRegistrationRequest protoRequest,
      StreamObserver<AnimalInfoResponse> responseObserver) {

    // 1. Convert from Protobuf Message -> DTO
    // We must convert here, because your service layer (correctly)
    // speaks in DTOs, not Protobuf messages.
    AnimalRegistrationRequestDTO requestDTO = protoToDto(protoRequest);

    // 2. Call your existing business logic service
    AnimalInfoResponseDTO responseDTO = animalService.createAnimal(requestDTO);

    // 3. Convert from DTO -> Protobuf Message
    AnimalInfoResponse protoResponse = dtoToProto(responseDTO);

    // 4. Send the gRPC response
    responseObserver.onNext(protoResponse);
    responseObserver.onCompleted();

    // TODO: You should add try/catch logic here for exceptions
    // (e.g., if validation fails or the animal regNo is a duplicate)
    // and return a gRPC error.
  }

  // --- Private Mapper Methods ---
  // You could also move these to a new "GrpcMapper" class

  private AnimalRegistrationRequestDTO protoToDto(AnimalRegistrationRequest proto) {
    AnimalRegistrationRequestDTO dto = new AnimalRegistrationRequestDTO();
    dto.setWeight(proto.getWeight());
    dto.setRegNo(proto.getRegNo());
    dto.setOrigin(proto.getOrigin());
    dto.setDate(LocalDate.of(
        proto.getDate().getYear(),
        proto.getDate().getMonth(),
        proto.getDate().getDay()
    ));
    return dto;
  }

  private AnimalInfoResponse dtoToProto(AnimalInfoResponseDTO dto) {
    LocalDate localDate = dto.getRegistrationDate();
    Date protoDate = Date.newBuilder()
        .setYear(localDate.getYear())
        .setMonth(localDate.getMonthValue())
        .setDay(localDate.getDayOfMonth())
        .build();

    return AnimalInfoResponse.newBuilder()
        .setAnimalId(dto.getAnimalid())
        .setWeight(dto.getWeight())
        .setRegNo(dto.getRegNo())
        .setOrigin(dto.getOrigin())
        .setRegistrationDate(protoDate)
        .setIsButchered(dto.isButchered())
        .build();
  }
}