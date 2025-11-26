package pro3.shared_dtos.dtos.Tray;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pro3.shared_dtos.dtos.extra.PartType;

public class TrayCreationRequestDTO {

  @NotNull(message = "Tray type cannot be null")
  private PartType type;

  @Positive(message = "Max weight capacity must be a positive number")
  private double maxWeightCapacity;

  public TrayCreationRequestDTO() {
  }

  public TrayCreationRequestDTO(PartType type, double maxWeightCapacity) {
    this.type = type;
    this.maxWeightCapacity = maxWeightCapacity;
  }

  public PartType getType() {
    return type;
  }

  public void setType(PartType type) {
    this.type = type;
  }

  public double getMaxWeightCapacity() {
    return maxWeightCapacity;
  }

  public void setMaxWeightCapacity(double maxWeightCapacity) {
    this.maxWeightCapacity = maxWeightCapacity;
  }
}
