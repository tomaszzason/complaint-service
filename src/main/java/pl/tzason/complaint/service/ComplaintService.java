package pl.tzason.complaint.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.dto.PageResponse;

public interface ComplaintService {
    ComplaintDTO createComplaint(ComplaintRequest complaintRequest, HttpServletRequest request);
    ComplaintDTO updateComplaint(Long id, ComplaintRequest complaintRequest, HttpServletRequest request);
    ComplaintDTO getComplaintById(Long id);
    PageResponse<ComplaintDTO> getAllComplaints(Pageable pageable);
    PageResponse<ComplaintDTO> getComplaintsByCountry(String country, Pageable pageable);
}
