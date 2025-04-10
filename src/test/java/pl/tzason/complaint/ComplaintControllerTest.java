package pl.tzason.complaint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.dto.PageResponse;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // testing endpoints with MockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@WithMockUser(username = "admin")
public class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldSubmitComplaint() throws Exception {

        ComplaintRequest complaintRequest = ComplaintRequest.builder()
                .productId("PROD-003")
                .reporter("tzason@example.com")
                .content("Some issue")
                .build();

        mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complaintRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldSubmitExistingComplaint() throws Exception {

        String PRODUCT_ID = "PROD-002";
        String REPORTER = "tzason@example.de";
        String CONTENT = "Description updated.";

        ComplaintRequest complaintRequest = ComplaintRequest.builder()
                .productId(PRODUCT_ID)
                .reporter(REPORTER)
                .content(CONTENT)
                .build();

        MvcResult result = mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complaintRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ComplaintDTO complaint = objectMapper.readValue(json, ComplaintDTO.class);

        assertEquals(REPORTER, complaint.getReporter());
        assertEquals(PRODUCT_ID, complaint.getProductId());
        assertEquals(CONTENT, complaint.getContent());
        assertEquals(2, complaint.getCounter());
    }

    @Test
    public void shouldUpdateComplaint() throws Exception {

        String PRODUCT_ID = "PROD-002";
        String REPORTER = "tzason@example.de";
        String CONTENT = "Description updated.";

        ComplaintRequest complaintRequest = ComplaintRequest.builder()
                .productId(PRODUCT_ID)
                .reporter(REPORTER)
                .content(CONTENT)
                .build();

        MvcResult result = mockMvc.perform(put("/api/complaints/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complaintRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ComplaintDTO complaint = objectMapper.readValue(json, ComplaintDTO.class);

        assertEquals(REPORTER, complaint.getReporter());
        assertEquals(PRODUCT_ID, complaint.getProductId());
        assertEquals(CONTENT, complaint.getContent());
        assertEquals(1, complaint.getCounter());
    }

    @Test
    public void shouldGetComplaint() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ComplaintDTO complaint = objectMapper.readValue(json, ComplaintDTO.class);

        assertEquals("tzason@example.com", complaint.getReporter());
        assertEquals("PROD-001", complaint.getProductId());
        assertEquals("Produkt nie działa zgodnie z opisem.", complaint.getContent());
    }

    @Test
    public void shouldGetComplaintByCountry() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints/country/PL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        PageResponse<ComplaintDTO> complaint = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(1, complaint.getContent().size());
        assertEquals("PL", complaint.getContent().get(0).getCountry());
        assertEquals("PROD-001", complaint.getContent().get(0).getProductId());
        assertEquals("Produkt nie działa zgodnie z opisem.", complaint.getContent().get(0).getContent());
    }


    @Test
    public void shouldGetAllComplaints() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        PageResponse<ComplaintDTO> complaint = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(2, complaint.getContent().size());
    }
}