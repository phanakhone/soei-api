package com.example.soeiapi.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.soeiapi.repositories.UserRepository;

@Service
public class ReportService {
    private final UserService userService;
    private final CompanyService companyService;
    private final UserRepository userRepository;

    public ReportService(UserService userService, CompanyService companyService, UserRepository userRepository) {
        this.userService = userService;
        this.companyService = companyService;
        this.userRepository = userRepository;
    }

    public Map<String, Map<Object, Object>> getUserCountReport() {

        List<Object[]> userCounts = userRepository.countUsersByCompanyAndRole();

        List<Map<String, Object>> userCountReport = userCounts.stream()
                .map(record -> Map.of(
                        "company_short_name", record[0].toString(),
                        "role_id", record[1].toString(),
                        "total", record[2]))
                .collect(Collectors.toList());

        // userCountReport.addAll(List.of(
        // Map.of("company_short_name", "agl", "role_id", 1, "total", 1),
        // Map.of("company_short_name", "agl", "role_id", 2, "total", 4),
        // Map.of("company_short_name", "agl", "role_id", 3, "total", 1),
        // Map.of("company_short_name", "agl", "role_id", 4, "total", 6),
        // Map.of("company_short_name", "toko", "role_id", 1, "total", 2),
        // Map.of("company_short_name", "toko", "role_id", 2, "total", 3)));

        if (userCountReport.isEmpty()) {
            return Map.of("message", Map.of());
        }

        // Transform the data into the desired format
        Map<String, Map<Object, Object>> transformedReport = userCountReport.stream()
                .collect(Collectors.groupingBy(
                        record -> (String) record.get("company_short_name"), // Group by company_short_name
                        Collectors.toMap(
                                record -> record.get("role_id"), // Use role_id as the key
                                record -> record.get("total") // Use total as the value
                        )));

        return transformedReport;
    }

}
