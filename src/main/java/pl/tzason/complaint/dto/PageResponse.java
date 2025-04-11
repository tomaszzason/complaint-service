package pl.tzason.complaint.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
}