package nus.iss.se.team9.admin_service_team9;

import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.AdminRepository;
import nus.iss.se.team9.admin_service_team9.service.AdminService;
import nus.iss.se.team9.admin_service_team9.service.RecipeService;
import nus.iss.se.team9.admin_service_team9.service.ReportService;
import nus.iss.se.team9.admin_service_team9.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RecipeService recipeService;

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @Value("${email.service.url}")
    private String emailServiceUrl = "http://localhost:8080"; // 用于测试的假设值

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAdmin() {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setUsername("admin1");
        when(adminRepository.findAll()).thenReturn(Collections.singletonList(admin));

        List<Admin> result = adminService.getAllAdmin();
        assertEquals(1, result.size());
        assertEquals("admin1", result.getFirst().getUsername());
    }

    @Test
    void testRejectRecipeReport() {
        adminService.rejectRecipeReport(1);

        verify(reportService, times(1)).rejectRecipeReportById(1);
    }

    @Test
    void testApproveMemberReport() {
        MemberReport memberReport = mock(MemberReport.class);
        Member member = mock(Member.class);
        EmailDetails emailDetails = new EmailDetails();

        when(memberReport.getMemberReported()).thenReturn(member);
        when(member.getId()).thenReturn(1);
        when(member.getEmail()).thenReturn("member@example.com");
        when(member.getUsername()).thenReturn("memberuser");
        when(reportService.getMemberReportById(1)).thenReturn(memberReport);

        adminService.approveMemberReport(1);

        verify(userService, times(1)).deleteMember(1);
        verify(reportService, times(1)).approveMemberReportById(1);
    }

    @Test
    void testRejectMemberReport() {
        adminService.rejectMemberReport(1);

        verify(reportService, times(1)).rejectMemberReportById(1);
    }
}
