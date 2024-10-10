package nus.iss.se.team9.admin_service_team9.service;

import nus.iss.se.team9.admin_service_team9.model.Member;
import nus.iss.se.team9.admin_service_team9.model.MemberReport;
import nus.iss.se.team9.admin_service_team9.model.RecipeReport;
import nus.iss.se.team9.admin_service_team9.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${report.service.url}")
    private String reportServiceUrl;

    public List<MemberReport> getReportsByMemberReported(Member member) {
        String url = reportServiceUrl + "/getMemberReportsByMemberReported";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Member> requestEntity = new HttpEntity<>(member, headers);
            ResponseEntity<List<MemberReport>> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {}
            );

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<RecipeReport> getAllPendingRecipeReports() {
        String url = reportServiceUrl + "/getAllRecipeReports";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<List<RecipeReport>> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<MemberReport> getAllPendingMemberReports() {
        String url = reportServiceUrl + "/getAllMemberReports";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<List<MemberReport>> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
