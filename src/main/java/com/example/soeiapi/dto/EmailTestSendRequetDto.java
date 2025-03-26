package com.example.soeiapi.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmailTestSendRequetDto {
    private String to;
    private List<String> cc;
}
