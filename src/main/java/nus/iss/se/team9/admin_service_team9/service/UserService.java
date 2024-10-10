package nus.iss.se.team9.admin_service_team9.service;

import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class UserService {

    private final RestTemplate restTemplate;
    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${user.service.url}")
    private String userServiceUrl;

    public Member getMemberById(int id) {
        String url = userServiceUrl + "/member/" + id;
        try {
            ResponseEntity<Member> response = restTemplate.exchange(url, HttpMethod.GET, null, Member.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Member not found with ID: " + id);
            return null;
        } catch (HttpClientErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            throw e;
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving member: " + e.getMessage());
            throw new RuntimeException("Error occurred while retrieving member: " + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteMember(Integer memberId) {
        String url = userServiceUrl + "/member/" + memberId;
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return ResponseEntity.ok("Member deleted successfully.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Failed to delete member.");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    public List<Member> getAllMembers() {
        String url = userServiceUrl + "/getAllMembersNotDeleted";
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}
