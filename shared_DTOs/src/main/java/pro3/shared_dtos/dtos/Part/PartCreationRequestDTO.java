package pro3.shared_dtos.dtos.Part;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import pro3.shared_dtos.dtos.extra.PartType;

//since the process is automatic, the validators are extra,
// but it's a good practice imo
public class PartCreationRequestDTO
{
  @NotBlank(message = "Part type must be declared")
  private PartType type;
  @NotBlank(message = "Animal id cannot be empty")
  private int animalId;
  @Positive(message = "Weight must be a positive number")
  private double weight;

  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public int getAnimalId()
  {
    return animalId;
  }

  public void setAnimalId(int animalId)
  {
    this.animalId = animalId;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }
}
