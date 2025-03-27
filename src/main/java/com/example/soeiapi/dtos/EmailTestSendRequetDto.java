package com.example.soeiapi.dtos;

import java.util.List;

import lombok.Data;

@Data
public class EmailTestSendRequetDto {
    private String to;
    private List<String> cc;
}
