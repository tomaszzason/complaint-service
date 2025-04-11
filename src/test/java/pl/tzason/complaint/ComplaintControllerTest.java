package pl.tzason.complaint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.dto.PageResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // testing endpoints with MockMvc
@WithMockUser(username = "admin")
@ActiveProfiles("test")
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

        assertEquals(REPORTER, complaint.reporter());
        assertEquals(PRODUCT_ID, complaint.productId());
        assertEquals(CONTENT, complaint.content());
        assertEquals(2, complaint.counter());
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

        assertEquals(REPORTER, complaint.reporter());
        assertEquals(PRODUCT_ID, complaint.productId());
        assertEquals(CONTENT, complaint.content());
        assertEquals(1, complaint.counter());
    }

    @Test
    public void shouldGetComplaint() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ComplaintDTO complaint = objectMapper.readValue(json, ComplaintDTO.class);

        assertEquals("tzason@example.com", complaint.reporter());
        assertEquals("PROD-001", complaint.productId());
        assertEquals("Produkt nie działa zgodnie z opisem.", complaint.content());
    }

    @Test
    public void shouldGetComplaintByCountry() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints/country/PL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        PageResponse<ComplaintDTO> complaint = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(1, complaint.content().size());
        assertEquals("PL", complaint.content().get(0).country());
        assertEquals("PROD-001", complaint.content().get(0).productId());
        assertEquals("Produkt nie działa zgodnie z opisem.", complaint.content().get(0).content());
    }


    @Test
    public void shouldGetAllComplaints() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        PageResponse<ComplaintDTO> complaint = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(2, complaint.content().size());
    }

    @Test
    public void shouldFailOnUpdateWhenComplaintNotFound() throws Exception {

        mockMvc.perform(get("/api/complaints/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}