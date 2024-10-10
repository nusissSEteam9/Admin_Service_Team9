package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final RestTemplate restTemplate;
    private final RecipeService recipeService;
    private final ReportService reportService;
    private final UserService userService;
    private final String emailServiceUrl;
    @Autowired
    public AdminService(RestTemplate restTemplate,
                        RecipeService recipeService,
                        ReportService reportService,
                        UserService userService,
                        AdminRepository adminRepository,
                        @Value("${email.service.url}") String emailServiceUrl) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.recipeService = recipeService;
        this.reportService = reportService;
        this.emailServiceUrl = emailServiceUrl;
        this.adminRepository = adminRepository;
    }

    public List<Admin> getAllAdmin(){
        return adminRepository.findAll();
    }

    public void approveRecipeReport(Integer reportId) {
        // get report
        RecipeReport recipeReport = reportService.getRecipeReportById(reportId);

        // generate email and send
        EmailDetails emailDetails = new EmailDetails();
        assert recipeReport != null;
        emailDetails.setTo(recipeReport.getRecipeReported().getMember().getEmail());
        emailDetails.setSubject("Recipe Deleted! ");
        emailDetails.setBody("Dear member " + recipeReport.getMember().getUsername() + ",\n" + "Your recipe "+recipeReport.getRecipeReported()+" has been deleted!\n"
                + "Please contact us if any question!");
        sendEmail(emailDetails);

        //delete recipe
        Recipe recipe = recipeReport.getRecipeReported();
        try {
            recipeService.deleteRecipe(recipe.getId());
            System.out.println("Recipe with ID " + recipe.getId() + " deleted successfully.");
        } catch (Exception e) {
            System.out.println("An error occurred while trying to delete the recipe: " + e.getMessage());
        }

        //approve report
        try {
            reportService.approveRecipeReportById(reportId);
            System.out.println("Recipe report approved successfully.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error occurred during approval: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during report approval.");
        }
    }

    public void rejectRecipeReport(Integer reportId) {
        try {
            reportService.rejectRecipeReportById(reportId);
            System.out.println("Recipe report rejected successfully.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error occurred during rejecting: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during report rejecting.");
        }
    }

    public void approveMemberReport(Integer reportId) {
        // get report
        MemberReport memberReport = reportService.getMemberReportById(reportId);

        // generate email and send
        EmailDetails emailDetails = new EmailDetails();
        assert memberReport != null;
        emailDetails.setTo(memberReport.getMemberReported().getEmail());
        emailDetails.setSubject("Account Deleted！");
        emailDetails.setBody("Dear member " + memberReport.getMemberReported().getUsername() + ",\n"
                + "You Account has been deleted"+",\n"
                + "Please contact us if any questions!");
        sendEmail(emailDetails);

        //delete member
        Member member = memberReport.getMemberReported();
        try {
            userService.deleteMember(member.getId());
            System.out.println("Member with ID " + member.getId() + " deleted successfully.");
        } catch (Exception e) {
            System.out.println("An error occurred while trying to delete the member: " + e.getMessage());
        }

        //approve report
        try {
            reportService.approveMemberReportById(reportId);
            System.out.println("Recipe report approved successfully.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error occurred during approval: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during report approval.");
        }
    }

    public void rejectMemberReport(Integer reportId) {
        try {
            reportService.rejectMemberReportById(reportId);
            System.out.println("Member report rejected successfully.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error occurred during rejecting: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during report rejecting.");
        }
    }

    public void sendEmail(EmailDetails emailDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
        String url = emailServiceUrl + "/sendEmailOTP";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Email sent successfully.");
            } else {
                System.out.println("Failed to send email. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

