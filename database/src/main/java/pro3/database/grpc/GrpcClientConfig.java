package pro3.database.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import slaughterhouse.assignment.grpc.AnimalServiceGrpc;

@Configuration
public class GrpcClientConfig {

  @Value("${grpc.animal-service.host:localhost}")
  private String host;

  @Value("${grpc.animal-service.port:9090}")
  private int port;

  @Bean
  public ManagedChannel animalServiceChannel() {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext() // use TLS in production if needed
        .build();
  }

  @Bean
  public AnimalServiceGrpc.AnimalServiceBlockingStub animalServiceBlockingStub(
      ManagedChannel animalServiceChannel) {
    return AnimalServiceGrpc.newBlockingStub(animalServiceChannel);
  }
}


