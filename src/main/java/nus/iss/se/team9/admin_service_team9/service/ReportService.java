package nus.iss.se.team9.admin_service_team9.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReportService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${report.service.url}")
    private String reportServiceUrl;

}
