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
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeReportRepository recipeReportRepository;
    @Autowired
    private MemberReportRepository memberReportRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findByMemberStatusNot(Status.DELETED);
    }

    // Searching and Filtering methods
    public Member getMemberById(Integer id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElse(null);
    }

    public List<MemberReport> getReportsByMember(Member member) {
        return memberReportRepository.findByMemberReportedAndStatus(member, Status.APPROVED);
    }
    // delete member and invalid the recipes he or she uploaded
    public void deleteMember(Integer memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setMemberStatus(Status.DELETED);
            memberRepository.save(member);
            List<Recipe> recipes = member.getAddedRecipes();
            for (Recipe recipe : recipes) {
                recipe.setStatus(Status.DELETED);
                recipeRepository.save(recipe);
            }
        }
    }
    // Pending RecipeReportList Operation
    public List<RecipeReport> getPendingRecipeReport() {
        List<RecipeReport> pendingReport = recipeReportRepository.findByStatus(Status.PENDING);
        return pendingReport;
    }

    public RecipeReport getRecipeReportById(Integer id) {
        Optional<RecipeReport> report = recipeReportRepository.findById(id);
        return report.orElse(null);
    }

    public void approveRecipeReport(Integer reportId) {
        RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(recipeReport.getRecipeReported().getMember().getEmail());
        emailDetails.setSubject("Recipe Deleted! ");
        emailDetails.setBody("Dear member " + recipeReport.getMember().getUsername() + ",\n" + "Your recipe "+recipeReport.getRecipeReported()+" has been deleted!\n"
                + "Please contact us if any question!");
        sendEmail(emailDetails);
        Recipe recipe = recipeReport.getRecipeReported();
        recipe.setStatus(Status.DELETED);
        recipeReport.setStatus(Status.APPROVED);
        recipeRepository.save(recipe);
        recipeReportRepository.save(recipeReport);
    }
    public void rejectRecipeReport(Integer reportId) {
        RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
        recipeReport.setStatus(Status.REJECTED);
    }

    public List<MemberReport> getPendingMemberReport() {
        List<MemberReport> pendingReport = memberReportRepository.findByStatus(Status.PENDING);
        return pendingReport;
    }
    public MemberReport getMemberReportById(Integer id) {
        Optional<MemberReport> report = memberReportRepository.findById(id);
        return report.orElse(null);
    }

    public void approveMemberReport(Integer reportId) {
        MemberReport memberReport = memberReportRepository.findById(reportId).orElse(null);
        EmailDetails emailDetails = new EmailDetails();
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
        memberReport.setStatus(Status.REJECTED);
    }

    public void sendEmail(EmailDetails emailDetails){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
        String url = emailServiceUrl;
        ResponseEntity<String> emailResponse = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
}

