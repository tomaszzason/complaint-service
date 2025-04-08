package pl.tzason.complaint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    private Long id;
    private String productId;
    private String content;
    private LocalDateTime createdDate;
    private String reporter;
    private String country;
    private Integer counter;
    private LocalDateTime lastModified;
}
