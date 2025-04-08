package pl.tzason.complaint.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "reporter", nullable = false)
    private String reporter;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "counter", nullable = false)
    private Integer counter;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModified = LocalDateTime.now();
        if (counter == null) {
            counter = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }
}