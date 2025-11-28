package pro3.database.grpc;

// Import the gRPC-Spring-Boot starter annotation
import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro3.database.entities.Animal;
import pro3.database.entities.Part;
import pro3.database.repository.AnimalRepository;
import pro3.database.repository.PartRepository;
import pro3.database.repository.ProductRepository;
import pro3.database.services.AnimalService; // <-- Using your new service!

// Import the generated classes from your .proto file
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import slaughterhouse.assignment.grpc.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the gRPC service defined in traceability.proto.
 * It uses the AnimalService to get data from the database.
 */
@Service
@GrpcService
public class TraceabilityServiceImpl extends TraceabilityServiceGrpc.TraceabilityServiceImplBase {

    private final AnimalRepository animalRepository;
    private final PartRepository partRepository;
    private final ProductRepository productRepository;

    public TraceabilityServiceImpl(AnimalRepository animalRepository,
                                   PartRepository partRepository,
                                   ProductRepository productRepository) {
        this.animalRepository = animalRepository;
        this.partRepository = partRepository;
        this.productRepository = productRepository;
    }

    /**
     * R1: Given a product ID, return the registration numbers of all animals
     * that contributed parts to that final product.
     */
    @Override
    public void getAnimalRegNosByProduct(ProductId request,
                                         StreamObserver<AnimalRegNoList> responseObserver) {

        int productId = request.getId();

        // 1. Find all parts that belong to this final product
        List<Part> parts = partRepository.findByProductId(productId);

        if (parts.isEmpty()) {
            // If no parts are found, return an empty list gracefully
            AnimalRegNoList response = AnimalRegNoList.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // 2. Get distinct Animal IDs from these parts
        Set<Integer> animalIds = parts.stream()
                .map(Part::getAnimalId)
                .collect(Collectors.toSet());

        // 3. Look up Animal RegNos for the distinct IDs
        List<AnimalRegNo> regNos = animalRepository.findAllById(animalIds).stream()
                .map(animal -> AnimalRegNo.newBuilder().setRegNo(animal.getRegNo()).build())
                .collect(Collectors.toList());

        // 4. Build and send response
        AnimalRegNoList response = AnimalRegNoList.newBuilder()
                .addAllRegNos(regNos)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * R2: Given an animal (by ID or regNo), return identifiers for all final
     * products that contain parts from that animal.
     */
    @Override
    public void getProductsByAnimal(AnimalLookup request,
                                    StreamObserver<ProductIdentifierList> responseObserver) {

        // 1. Resolve the animal ID, regardless of which field was set in the oneof
        int animalId;

        if (request.hasId()) {
            animalId = request.getId();
        } else if (request.hasRegNo()) {
            String regNo = request.getRegNo();
            Animal animal = animalRepository.findByRegNo(regNo)
                    .orElse(null);

            if (animal == null) {
                // No such animal -> return empty list
                ProductIdentifierList empty = ProductIdentifierList.newBuilder().build();
                responseObserver.onNext(empty);
                responseObserver.onCompleted();
                return;
            }
            animalId = animal.getId();
        } else {
            // Neither id nor reg_no provided -> empty response
            ProductIdentifierList empty = ProductIdentifierList.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
            return;
        }

        // 2. Find all parts that originated from this animal
        List<Part> parts = partRepository.findByAnimalId(animalId);

        // 3. Get distinct Product IDs from these parts that have been packaged
        Set<Integer> packagedProductIds = parts.stream()
                .filter(part -> part.getProductId() != null) // Only include parts used in a final product
                .map(Part::getProductId)
                .collect(Collectors.toSet());

        if (packagedProductIds.isEmpty()) {
            ProductIdentifierList empty = ProductIdentifierList.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
            return;
        }

        // 4. Look up the Product entities to determine their type
        List<ProductIdentifier> products = productRepository.findAllById(packagedProductIds).stream()
                .map(product -> ProductIdentifier.newBuilder()
                        .setProductId(product.getId())
                        // product.getClass().getSimpleName() gives us "SameTypeProduct" or "HalfAnimalProduct"
                        .setProductType(product.getClass().getSimpleName())
                        .build())
                .collect(Collectors.toList());

        // 5. Build and send response
        ProductIdentifierList response = ProductIdentifierList.newBuilder()
                .addAllProducts(products)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}