package pro3.shared_dtos.dtos.Product;

import jakarta.validation.constraints.NotEmpty;
import pro3.shared_dtos.dtos.Part.PartInfoResponseDTO;

import java.util.List;

public class HalfAnimalProductCreationRequestDTO
{

  // We only need the IDs of the parts, not the full DTOs.
  @NotEmpty(message = "Product must be created with at least one part.")
  private List<Integer> partIds;

  // --- Getters and Setters ---

  public List<Integer> getPartIds() {
    return partIds;
  }

  public void setPartIds(List<Integer> partIds) {
    this.partIds = partIds;
  }

  public void addPartId(int partId)
  {
    this.partIds.add(partId);
  }
}
