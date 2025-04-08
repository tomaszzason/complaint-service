package pl.tzason.complaint.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.tzason.complaint.model.Complaint;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByProductIdAndReporter(String productId, String reporter);

    Page<Complaint> findAll(Pageable pageable);

    Page<Complaint> findByCountry(String country, Pageable pageable);

    @Modifying
    @Query("UPDATE Complaint c SET c.counter = c.counter + 1 WHERE c.productId = :productId AND c.reporter = :reporter")
    void incrementCounter(@Param("productId") String productId, @Param("reporter") String reporter);
}