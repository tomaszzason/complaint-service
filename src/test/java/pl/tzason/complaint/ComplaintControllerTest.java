package pl.tzason.complaint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.tzason.complaint.config.SecurityConfig;
import pl.tzason.complaint.controller.ComplaintController;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.service.ComplaintService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComplaintController.class)
@Import(SecurityConfig.class)
public class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin")
    public void shouldSubmitComplaint() throws Exception {
        ComplaintDTO complaintDto = ComplaintDTO.builder()
                .productId("P123")
                .reporter("testUser")
                .content("Some issue")
                .createdDate(LocalDateTime.now())
                .country("PL")
                .counter(1)
                .build();

        ComplaintRequest complaintRequest = ComplaintRequest.builder()
                .productId("P123")
                .reporter("testUser")
                .content("Some issue")
                .build();

        when(service.createComplaint(eq(complaintRequest), any(HttpServletRequest.class))).thenReturn(complaintDto);

        mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complaintRequest)))
                .andExpect(status().isCreated());
    }
}