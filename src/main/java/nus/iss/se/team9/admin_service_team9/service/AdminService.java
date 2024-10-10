package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {
    @Value("${email.service.url}")
    private String emailServiceUrl;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecipeReportRepository recipeReportRepository;
    @Autowired
    private MemberReportRepository memberReportRepository;
    @Autowired
    private RecipeService recipeService;

    // Pending RecipeReportList Operation
    public List<RecipeReport> getPendingRecipeReport() {
        return recipeReportRepository.findByStatus(Status.PENDING);
    }

    public RecipeReport getRecipeReportById(Integer id) {
        Optional<RecipeReport> report = recipeReportRepository.findById(id);
        return report.orElse(null);
    }

    public void approveRecipeReport(Integer reportId) {
        RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
        EmailDetails emailDetails = new EmailDetails();
        assert recipeReport != null;
        emailDetails.setTo(recipeReport.getRecipeReported().getMember().getEmail());
        emailDetails.setSubject("Recipe Deleted! ");
        emailDetails.setBody("Dear member " + recipeReport.getMember().getUsername() + ",\n" + "Your recipe "+recipeReport.getRecipeReported()+" has been deleted!\n"
                + "Please contact us if any question!");
        sendEmail(emailDetails);
        Recipe recipe = recipeReport.getRecipeReported();
        recipeService.deleteRecipe(recipe.getId());
        recipeReport.setStatus(Status.APPROVED);
        recipeReportRepository.save(recipeReport);
    }
    public void rejectRecipeReport(Integer reportId) {
        RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
        assert recipeReport != null;
        recipeReport.setStatus(Status.REJECTED);
    }

    public List<MemberReport> getPendingMemberReport() {
        return memberReportRepository.findByStatus(Status.PENDING);

    }
    public MemberReport getMemberReportById(Integer id) {
        Optional<MemberReport> report = memberReportRepository.findById(id);
        return report.orElse(null);
    }

    public void approveMemberReport(Integer reportId) {
        MemberReport memberReport = memberReportRepository.findById(reportId).orElse(null);
        EmailDetails emailDetails = new EmailDetails();
        assert memberReport != null;
        emailDetails.setTo(memberReport.getMemberReported().getEmail());
        emailDetails.setSubject("Account Deleted！");
        emailDetails.setBody("Dear member " + memberReport.getMemberReported().getUsername() + ",\n"
                + "You Account has been deleted"+",\n"
                + "Please contact us if any questions!");
        sendEmail(emailDetails);

        Member member = memberReport.getMemberReported();
        member.setMemberStatus(Status.DELETED);
        memberReport.setStatus(Status.APPROVED);
        memberRepository.save(member);
        memberReportRepository.save(memberReport);
    }

    public void rejectMemberReport(Integer reportId) {
        MemberReport memberReport = memberReportRepository.findById(reportId).orElse(null);
        assert memberReport != null;
        memberReport.setStatus(Status.REJECTED);
    }

    public void sendEmail(EmailDetails emailDetails){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
        String url = emailServiceUrl + "/sendEmailOTP";
        ResponseEntity<String> emailResponse = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
}

