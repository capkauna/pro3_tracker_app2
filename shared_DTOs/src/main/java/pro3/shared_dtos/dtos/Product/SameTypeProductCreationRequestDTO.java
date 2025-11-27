package pro3.shared_dtos.dtos.Product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import pro3.shared_dtos.dtos.Part.PartInfoResponseDTO;
import pro3.shared_dtos.dtos.extra.PartType;

import java.util.List;

public class SameTypeProductCreationRequestDTO
{
  @NotNull(message = "PartType cannot be null")
  private PartType type;

  // We only need the IDs of the parts, not the full DTOs.
  @NotEmpty(message = "Product must be created with at least one part.")
  private List<Integer> partIds;


  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public List<Integer> getPartIds() {
    return partIds;
  }

  public void setPartIds(List<Integer> partIds) {
    this.partIds = partIds;
  }

  public void addPart (int partId)
  {
    this.partIds.add(partId);
  }
}
