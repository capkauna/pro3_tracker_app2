package pro3.database.grpc;

import com.google.type.Date;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import pro3.database.services.AnimalService;
import pro3.shared_dtos.dtos.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.grpc.AnimalsInfoResponse;
import slaughterhouse.assignment.grpc.AnimalsListResponse;
import slaughterhouse.assignment.grpc.AnimalsRegistrationRequest;
import slaughterhouse.assignment.grpc.AnimalsServiceGrpc;
import slaughterhouse.assignment.grpc.GetAnimalsByDateRequest;
import slaughterhouse.assignment.grpc.GetAnimalsByRegNoRequest;
import slaughterhouse.assignment.grpc.GetAnimalsByOriginRequest;
import slaughterhouse.assignment.grpc.MarkAnimalsButchered;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
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
    public void addAnimal(
            AnimalRegistrationRequest protoRequest,
            StreamObserver<AnimalInfoResponse> responseObserver
    ) {
        try {
            // Protobuf -> DTO
            AnimalRegistrationRequestDTO requestDTO = protoToDto(protoRequest);

            // Business logic
            AnimalInfoResponseDTO responseDTO = animalService.createAnimal(requestDTO);

            // DTO -> Protobuf
            AnimalInfoResponse protoResponse = dtoToProto(responseDTO);

            responseObserver.onNext(protoResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to add animal: " + e.getMessage())
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
            GetAnimalByDateRequest request,
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
        } catch (Exception e) {
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
            // This calls your existing service, which currently throws if ID is invalid
            AnimalInfoResponseDTO dto = animalService.markAnimalAsButchered(animalId);

            AnimalInfoResponse proto = dtoToProto(dto);
            responseObserver.onNext(proto);
            responseObserver.onCompleted();
        } catch (NoSuchElementException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Animal with id " + animalId + " not found")
                            .asRuntimeException()
            );
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
        dto.setDate(protoDateToLocalDate(proto.getDate()));
        return dto;
    }

    private AnimalInfoResponse dtoToProto(AnimalInfoResponseDTO dto) {
        LocalDate localDate = dto.getRegistrationDate();
        Date protoDate = localDateToProtoDate(localDate);

        return AnimalInfoResponse.newBuilder()
                .setAnimalId(dto.getAnimalid())
                .setWeight(dto.getWeight())
                .setRegNo(dto.getRegNo())
                .setOrigin(dto.getOrigin())
                .setRegistrationDate(protoDate)
                .setIsButchered(dto.isButchered())
                .build();
    }

    private LocalDate protoDateToLocalDate(Date protoDate) {
        return LocalDate.of(
                protoDate.getYear(),
                protoDate.getMonth(),
                protoDate.getDay()
        );
    }

    private Date localDateToProtoDate(LocalDate localDate) {
        return Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonthValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }
}
