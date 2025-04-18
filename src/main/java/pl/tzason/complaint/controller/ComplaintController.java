package pl.tzason.complaint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.tzason.complaint.dto.ComplaintDTO;
import pl.tzason.complaint.dto.ComplaintRequest;
import pl.tzason.complaint.dto.PageResponse;
import pl.tzason.complaint.service.ComplaintService;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Controller", description = "Endpoints for managing complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @Operation(summary = "Create a new complaint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Complaint created",
                    content = @Content(schema = @Schema(implementation = ComplaintDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintDTO createComplaint(
            @Valid @RequestBody ComplaintRequest complaintRequest,
            HttpServletRequest request) {
        return complaintService.createComplaint(complaintRequest, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing complaint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint updated",
                    content = @Content(schema = @Schema(implementation = ComplaintDTO.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ComplaintDTO updateComplaint(
            @Parameter(description = "Complaint ID") @PathVariable Long id,
            @Valid @RequestBody ComplaintRequest complaintRequest) {
        return complaintService.updateComplaint(id, complaintRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a complaint by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the complaint",
                    content = @Content(schema = @Schema(implementation = ComplaintDTO.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found")
    })
    public ComplaintDTO getComplaintById(
            @Parameter(description = "Complaint ID") @PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

    @GetMapping
    @Operation(summary = "Get all complaints with pagination")
    public PageResponse<ComplaintDTO> getAllComplaints(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return complaintService.getAllComplaints(pageable);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Get complaints by country with pagination")
    public PageResponse<ComplaintDTO> getComplaintsByCountry(
            @Parameter(description = "Country code") @PathVariable String country,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return complaintService.getComplaintsByCountry(country, pageable);

    }
}