package nus.iss.se.team9.admin_service_team9.service;

import nus.iss.se.team9.admin_service_team9.model.Member;
import nus.iss.se.team9.admin_service_team9.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public Member getMemberById(Integer id) {
        String url = userServiceUrl + "/member/" + id;
        ResponseEntity<Member> response = restTemplate.exchange(
                url, HttpMethod.GET, null, Member.class);
        return response.getBody();
    }

    public void deleteMember(Member member) {
        String url = userServiceUrl + "/member/" + member.getId();
        restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
    }
    public List<Member> getAllMembers() {
        String url = userServiceUrl + "/getAllMembersNotDeleted";
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Member>>() {});
        return response.getBody();
    }
}
