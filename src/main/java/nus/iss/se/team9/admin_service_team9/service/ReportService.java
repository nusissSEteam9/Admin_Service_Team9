package nus.iss.se.team9.admin_service_team9.service;

import nus.iss.se.team9.admin_service_team9.model.MemberReport;
import nus.iss.se.team9.admin_service_team9.model.RecipeReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final RestTemplate restTemplate;
    private final String reportServiceUrl;
    @Autowired
    public ReportService(RestTemplate restTemplate, @Value("${report.service.url}") String reportServiceUrl) {
        this.restTemplate = restTemplate;
        this.reportServiceUrl = reportServiceUrl;
    }

    public List<MemberReport> getReportsByMemberReported(Integer memberId) {
        String url = reportServiceUrl + "/getMemberReportsByMemberReported";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Integer> requestBody = new HashMap<>();
            requestBody.put("memberId", memberId);
            HttpEntity<Map<String, Integer>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<List<MemberReport>> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public RecipeReport getRecipeReportById(Integer id) {
        String url = reportServiceUrl + "/getRecipeReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<RecipeReport> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, RecipeReport.class
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public MemberReport getMemberReportById(Integer id) {
        String url = reportServiceUrl + "/getMemberReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<MemberReport> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, MemberReport.class
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void approveRecipeReportById(Integer id) {
        String url = reportServiceUrl + "/approveRecipeReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // 设置请求头为 JSON

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<RecipeReport> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, RecipeReport.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                response.getBody();
            } else {
                System.out.println("Failed to approve recipe report. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectRecipeReportById(Integer id) {
        String url = reportServiceUrl + "/rejectRecipeReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // 设置请求头为 JSON

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<RecipeReport> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, RecipeReport.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                response.getBody();
            } else {
                System.out.println("Failed to reject recipe report. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void approveMemberReportById(Integer id) {
        String url = reportServiceUrl + "/approveMemberReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // 设置请求头为 JSON

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<MemberReport> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, MemberReport.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                response.getBody();
            } else {
                System.out.println("Failed to approve member report. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectMemberReportById(Integer id) {
        String url = reportServiceUrl + "/rejectMemberReportById?id=" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<MemberReport> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, MemberReport.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                response.getBody();
            } else {
                System.out.println("Failed to reject member report. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
