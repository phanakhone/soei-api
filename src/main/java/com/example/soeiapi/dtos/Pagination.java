package com.example.soeiapi.dtos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private Integer currentPage;
    private Integer perPage;
    private Integer totalPages;
    private Integer totalItems;
    private Map<String, String> filters;
    private String sortBy;
    private String sortDirection;
}
