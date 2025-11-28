package pro3.shared_dtos.dtos.Product;

import pro3.shared_dtos.dtos.Part.PartInfoResponseDTO;

import java.util.List;

//no annotations needed here
public class HalfAnimalProductInfoResponseDTO extends ProductInfoResponseDTO
{
  private List<PartInfoResponseDTO> parts;

    public HalfAnimalProductInfoResponseDTO(int id, List<PartInfoResponseDTO> partDTOs) {
        super();
    }

    // --- Getters and Setters ---

  public List<PartInfoResponseDTO> getParts() {
    return parts;
  }

  public void setParts(List<PartInfoResponseDTO> parts) {
    this.parts = parts;
  }

}
