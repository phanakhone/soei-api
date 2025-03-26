package com.example.soeiapi.utils;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pagination {
    private Integer currentPage;
    private Integer perPage;
    private Integer totalPages;
    private Integer totalItems;
    private Map<String, String> filters;
    private String sortBy;
    private String sortDirection;
}
