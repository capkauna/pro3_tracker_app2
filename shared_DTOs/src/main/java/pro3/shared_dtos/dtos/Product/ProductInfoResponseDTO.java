package pro3.shared_dtos.dtos.Product;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Annotate the base class to include type information
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "productType" // This adds the "label" to the JSON
)
@JsonSubTypes({
    // List all the concrete "child" DTOs
    @JsonSubTypes.Type(value = SameTypeProductInfoResponseDTO.class, name = "SameTypeProduct"),
    @JsonSubTypes.Type(value = HalfAnimalProductInfoResponseDTO.class, name = "HalfAnimalProduct")
})
public abstract class ProductInfoResponseDTO {

  private int id;

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}