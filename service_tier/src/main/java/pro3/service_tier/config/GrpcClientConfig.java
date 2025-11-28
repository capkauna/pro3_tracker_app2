package pro3.service_tier.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import slaughterhouse.assignment.grpc.AnimalServiceGrpc;

@Configuration
public class GrpcClientConfig implements DisposableBean {

    private ManagedChannel channel;

    @Bean
    public ManagedChannel animalServiceChannel(
            @Value("${grpc.animal-service.host:localhost}") String host,
            @Value("${grpc.animal-service.port:9090}") int port) {

        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext() // fine for local dev; HTTPS/TLS not required in the assignment
                .build();
        return this.channel;
    }

    @Bean
    public AnimalServiceGrpc.AnimalServiceBlockingStub animalServiceStub(
            ManagedChannel animalServiceChannel) {
        return AnimalServiceGrpc.newBlockingStub(animalServiceChannel);
    }

    @Override
    public void destroy() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
