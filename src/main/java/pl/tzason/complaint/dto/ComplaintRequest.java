package pl.tzason.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {
    @NotBlank(message = "Product ID cannot be empty")
    private String productId;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotBlank(message = "Reporter cannot be empty")
    private String reporter;
}
