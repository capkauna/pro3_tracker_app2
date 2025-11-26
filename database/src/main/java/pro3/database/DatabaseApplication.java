package pro3.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories // Ensure JPA repositories are scanned
public class DatabaseApplication
{

  public static void main(String[] args)
  {
    SpringApplication.run(DatabaseApplication.class, args);
  }

}
