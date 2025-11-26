package pro3.database.grpc;

// Import the gRPC-Spring-Boot starter annotation
import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import pro3.database.services.AnimalService; // <-- Using your new service!

// Import the generated classes from your .proto file
import pro3.shared_dtos.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.grpc.*;

import java.util.Optional;

/**
 * This class implements the gRPC service defined in traceability.proto.
 * It uses the AnimalService to get data from the database.
 */
@GrpcService // This tells the gRPC starter this is an implementation
public class TraceabilityServiceImpl extends TraceabilityServiceGrpc.TraceabilityServiceImplBase {

  private final AnimalService animalService;
  // You will likely also need a ProductService here eventually
  // private final ProductService productService;

  @Autowired
  public TraceabilityServiceImpl(AnimalService animalService /*, ProductService productService */) {
    this.animalService = animalService;
    // this.productService = productService;
  }

  @Override
  public void getProductsByAnimal(AnimalLookup request, StreamObserver<ProductIdentifierList> responseObserver) {

    // --- This is just an example of how you'd use the service ---
    // 1. Check how the user is looking up the animal
    if (request.hasRegNo()) {
      String regNo = request.getRegNo();

      // 2. Use your service to find the animal
      Optional<AnimalInfoResponseDTO> animalOpt = animalService.getAnimalByRegNo(regNo);

      if (animalOpt.isPresent()) {
        int animalId = animalOpt.get().getAnimalid();

        // 3. TODO: Use a (future) ProductService to find products by animalId
        // List<ProductIdentifier> products = productService.getProductsByAnimalId(animalId);

        // 4. Build the response
        ProductIdentifierList response = ProductIdentifierList.newBuilder()
            // .addAllProducts(products)
            .build();

        responseObserver.onNext(response);
      } else {
        // Handle "not found" error
      }
    }

    responseObserver.onCompleted();
  }

  @Override
  public void getAnimalRegNosByProduct(ProductId request, StreamObserver<AnimalRegNoList> responseObserver) {
    // TODO:
    // 1. Use a (future) ProductService to find the product by request.getId()
    // 2. Get all animals associated with that product
    // 3. Build the AnimalRegNoList response

    // Example:
    // List<String> regNos = productService.getRegNosForProduct(request.getId());
    // List<AnimalRegNo> protoRegNos = regNos.stream()
    //     .map(regNo -> AnimalRegNo.newBuilder().setRegNo(regNo).build())
    //     .collect(Collectors.toList());
    //
    // AnimalRegNoList response = AnimalRegNoList.newBuilder()
    //     .addAllRegNos(protoRegNos)
    //     .build();
    //
    // responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}