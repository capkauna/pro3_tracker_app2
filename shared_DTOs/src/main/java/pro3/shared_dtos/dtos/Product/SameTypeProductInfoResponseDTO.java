package pro3.shared_dtos.dtos.Product;

import pro3.shared_dtos.dtos.Part.PartInfoResponseDTO;
import pro3.shared_dtos.dtos.extra.PartType;

import java.util.List;

//no annotations needed here
public class SameTypeProductInfoResponseDTO extends ProductInfoResponseDTO
{
  private PartType type;
  private List<PartInfoResponseDTO> parts;

  // Getters and setters

  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public List<PartInfoResponseDTO> getParts()
  {
    return parts;
  }

  public void setParts(List<PartInfoResponseDTO> parts)
  {
    this.parts = parts;
  }
}
