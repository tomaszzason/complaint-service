package pl.tzason.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ComplaintRequest (
    @NotBlank(message = "Product ID cannot be empty")
    String productId,

    @NotBlank(message = "Content cannot be empty")
    String content,

    @NotBlank(message = "Reporter cannot be empty")
    String reporter
) {
}
