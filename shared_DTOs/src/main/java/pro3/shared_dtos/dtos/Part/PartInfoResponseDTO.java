package pro3.shared_dtos.dtos.Part;

import pro3.shared_dtos.dtos.extra.PartType;

public class PartInfoResponseDTO
{
  private int id;
  private PartType type;
  private int animalId;
  private double weight;
  private int trayId;
  private boolean packaged;
  private int productId;

  public PartInfoResponseDTO()
  {
  }

  public PartInfoResponseDTO(
      int id,
      PartType type,
      int animalId,
      double weight,
      int trayId,
      boolean packaged,
      int productId)
  {
    this.type = type;
    this.animalId = animalId;
    this.weight = weight;
    this.trayId = trayId;
    this.packaged = packaged;
    this.productId = productId;
  }

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

  public int getTrayId()
  {
    return trayId;
  }

  public void setTrayId(int trayId)
  {
    this.trayId = trayId;
  }

  public boolean isPackaged()
  {
    return packaged;
  }

  public void setPackaged(boolean packaged)
  {
    this.packaged = packaged;
  }

  public int getProductId()
  {
    return productId;
  }

  public void setProductId(int productId)
  {
    this.productId = productId;
  }
}
