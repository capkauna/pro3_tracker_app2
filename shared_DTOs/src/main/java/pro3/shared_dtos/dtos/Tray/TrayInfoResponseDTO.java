package pro3.shared_dtos.dtos.Tray;

import pro3.shared_dtos.dtos.extra.PartType;

public class TrayInfoResponseDTO {

  private int id;
  private double maxWeightCapacity;
  private double currentWeight;
  private PartType type;
  private boolean packaged;
  private boolean full;

  public TrayInfoResponseDTO() {
  }

  public TrayInfoResponseDTO(int id,
      double maxWeightCapacity,
      double currentWeight,
      PartType type,
      boolean packaged,
      boolean full) {
    this.id = id;
    this.maxWeightCapacity = maxWeightCapacity;
    this.currentWeight = currentWeight;
    this.type = type;
    this.packaged = packaged;
    this.full = full;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getMaxWeightCapacity() {
    return maxWeightCapacity;
  }

  public void setMaxWeightCapacity(double maxWeightCapacity) {
    this.maxWeightCapacity = maxWeightCapacity;
  }

  public double getCurrentWeight() {
    return currentWeight;
  }

  public void setCurrentWeight(double currentWeight) {
    this.currentWeight = currentWeight;
  }

  public PartType getType() {
    return type;
  }

  public void setType(PartType type) {
    this.type = type;
  }

  public boolean isPackaged() {
    return packaged;
  }

  public void setPackaged(boolean packaged) {
    this.packaged = packaged;
  }

  public boolean isFull() {
    return full;
  }

  public void setFull(boolean full) {
    this.full = full;
  }
}
