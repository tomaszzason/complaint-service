package pl.tzason.complaint.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ComplaintResponse (
    Long id,
    String productId,
    String content,
    LocalDateTime createdDate,
    String reporter,
    String country,
    Integer counter,
    LocalDateTime lastModified
) {

}