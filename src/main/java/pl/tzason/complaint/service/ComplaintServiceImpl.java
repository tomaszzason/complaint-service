package pl.tzason.complaint.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.dto.PageResponse;
import pl.tzason.complaint.model.Complaint;
import pl.tzason.complaint.exception.ResourceNotFoundException;
import pl.tzason.complaint.repository.ComplaintRepository;
import pl.tzason.complaint.util.RequestUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CountryService countryService;

    @Override
    @Transactional
    public ComplaintDTO createComplaint(ComplaintRequest complaintRequest, HttpServletRequest request) {
        log.info("Creating new complaint for product: {}, reporter: {}", complaintRequest.getProductId(), complaintRequest.getReporter());

        Optional<Complaint> existingComplaint = complaintRepository.findByProductIdAndReporter(complaintRequest.getProductId(), complaintRequest.getReporter());

        if (existingComplaint.isPresent()) {
            log.info("Complaint already exists. Incrementing counter.");
            Complaint complaint = existingComplaint.get();
            complaint.setCounter(complaint.getCounter() + 1);
            complaintRepository.save(complaint);
            return mapToDTO(complaint);
        }

        String ipAddress = RequestUtil.getClientIp(request);
        String country = countryService.getCountryByIp(ipAddress);

        Complaint complaint = Complaint.builder()
                .productId(complaintRequest.getProductId())
                .content(complaintRequest.getContent())
                .reporter(complaintRequest.getReporter())
                .country(country)
                .counter(1)
                .createdDate(LocalDateTime.now()).build();

        Complaint savedComplaint = complaintRepository.save(complaint);
        log.info("Created new complaint with ID: {}", savedComplaint.getId());

        return mapToDTO(savedComplaint);
    }

    @Override
    @Transactional
    @CacheEvict(value = "complaints", key = "#id")
    public ComplaintDTO updateComplaint(Long id, ComplaintRequest complaintRequest, HttpServletRequest request) {
        log.info("Updating complaint with ID: {}", id);

        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        // Check if it's the same product and reporter
        if (!complaint.getProductId().equals(complaintRequest.getProductId()) ||
                !complaint.getReporter().equals(complaintRequest.getReporter())) {
            throw new IllegalArgumentException("Cannot change product ID or reporter in an existing complaint");
        }

        complaint.setContent(complaintRequest.getContent());

        Complaint updatedComplaint = complaintRepository.save(complaint);
        log.info("Updated complaint with ID: {}", id);

        return mapToDTO(updatedComplaint);
    }

    @Override
    @Cacheable(value = "complaints", key = "#id")
    public ComplaintDTO getComplaintById(Long id) {
        log.info("Fetching complaint with ID: {}", id);

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        return mapToDTO(complaint);
    }

    @Override
    @Cacheable(value = "complaints", key = "'all_'+#pageable.pageNumber+'_'+#pageable.pageSize")
    public PageResponse<ComplaintDTO> getAllComplaints(Pageable pageable) {
        log.info("Fetching all complaints with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Complaint> complaintPage = complaintRepository.findAll(pageable);

        return createPageResponse(complaintPage);
    }

    @Override
    @Cacheable(value = "complaints", key = "'country_'+#country+'_'+#pageable.pageNumber+'_'+#pageable.pageSize")
    public PageResponse<ComplaintDTO> getComplaintsByCountry(String country, Pageable pageable) {
        log.info("Fetching complaints for country: {} with pagination: page={}, size={}", country, pageable.getPageNumber(), pageable.getPageSize());

        Page<Complaint> complaintPage = complaintRepository.findByCountry(country, pageable);

        return createPageResponse(complaintPage);
    }

    private PageResponse<ComplaintDTO> createPageResponse(Page<Complaint> complaintPage) {
        return PageResponse.<ComplaintDTO>builder().content(complaintPage.getContent().stream()
                .map(this::mapToDTO).collect(Collectors.toList()))
                .pageNumber(complaintPage.getNumber())
                .pageSize(complaintPage.getSize())
                .totalElements(complaintPage.getTotalElements())
                .totalPages(complaintPage.getTotalPages())
                .last(complaintPage.isLast()).build();
    }

    private ComplaintDTO mapToDTO(Complaint complaint) {
        return ComplaintDTO.builder()
                .id(complaint.getId())
                .productId(complaint.getProductId())
                .content(complaint.getContent())
                .createdDate(complaint.getCreatedDate())
                .reporter(complaint.getReporter())
                .country(complaint.getCountry())
                .counter(complaint.getCounter())
                .lastModified(complaint.getLastModified())
                .build();
    }
}