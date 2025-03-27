package com.example.soeiapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private Pagination pagination;
    private Object error;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<List<T>> successList(String message, List<T> data, Pagination pagination) {
        return ApiResponse.<List<T>>builder()
                .status("success")
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }

    public static ApiResponse<Object> error(String message, Object error, int code) {
        return ApiResponse.builder()
                .status("error")
                .message(message)
                .error(error)
                .build();
    }
}
