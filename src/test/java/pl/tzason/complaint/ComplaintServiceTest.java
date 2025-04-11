package pl.tzason.complaint;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.model.Complaint;
import pl.tzason.complaint.repository.ComplaintRepository;
import pl.tzason.complaint.service.ComplaintService;
import pl.tzason.complaint.service.CountryService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComplaintServiceTest {

    private ComplaintService complaintService;
    private ComplaintRepository complaintRepository;
    private CountryService countryService;

    @BeforeEach
    public void setup() {
        complaintRepository = Mockito.mock(ComplaintRepository.class);
        countryService = Mockito.mock(CountryService.class);
        complaintService = new ComplaintService(complaintRepository, countryService);
    }

    @Test
    public void shouldIncreaseReportCountForDuplicateComplaint() {
        Complaint existing = Complaint.builder()
                .id(1L)
                .productId("P1")
                .reporter("user1")
                .content("desc")
                .createdDate(LocalDateTime.now())
                .country("PL")
                .counter(1)
                .build();

        when(complaintRepository.findByProductIdAndReporter("P1", "user1")).thenReturn(Optional.of(existing));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(existing);

        ComplaintRequest input = ComplaintRequest.builder()
                .productId("P1")
                .reporter("user1")
                .content("new desc")
                .build();

        HttpServletRequest request = mock(HttpServletRequest.class);
        ComplaintDTO result = complaintService.createComplaint(input, request);
        assertEquals(2, result.counter());
        verify(complaintRepository, times(1)).save(any());
    }
}