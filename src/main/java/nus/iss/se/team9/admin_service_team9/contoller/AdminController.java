package nus.iss.se.team9.admin_service_team9.contoller;
import nus.iss.se.team9.admin_service_team9.service.*;
import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final RecipeService recipeService;
    private final UserService userService;
    private final AdminService adminService;
    private final ReportService reportService;

    @Autowired
    public AdminController(RecipeService recipeService, UserService userService, AdminService adminService, ReportService reportService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.adminService = adminService;
        this.reportService = reportService;
    }

    @GetMapping("/health")
    public String checkHealth(){
        return "API is connected";
    }

    @GetMapping("/getAdminList")
    public ResponseEntity<List<Admin>> getAdminList() {
        System.out.println("Processing fetching all admins");
        try {
            List<Admin> admins = adminService.getAllAdmin();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/getDashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        System.out.println("Hi admin!");
        Map<String, Object> response = new HashMap<>();
        int year = 2003;
        List<Recipe> recipesByYear = recipeService.getAllRecipesByYear(year);
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        List<Integer> recipeCountByMonth = new ArrayList<>(Collections.nCopies(12, 0));
        for (Recipe recipe : recipesByYear) {
            int monthIndex = recipe.getSubmittedDate().getMonthValue() - 1;
            recipeCountByMonth.set(monthIndex, recipeCountByMonth.get(monthIndex) + 1);
        }
        response.put("months", months);
        response.put("recipeCountByMonth", recipeCountByMonth);

        List<Object[]> tagCounts = recipeService.getRecipeCountByTag();
        List<String> tags = new ArrayList<>();
        List<Long> recipeCountByTag = new ArrayList<>();
        Long tenthTagCount = 0L;

        for (int i = 0; i < tagCounts.size(); i++) {
            Object[] tagCount = tagCounts.get(i);
            String tag = (String) tagCount[0];
            // Convert recipeCount to Long if necessary
            Long recipeCount = ((Number) tagCount[1]).longValue();

            if (i < 10) {
                tags.add(tag);
                recipeCountByTag.add(recipeCount);
                tenthTagCount = recipeCount;
            } else {
                if (recipeCount.equals(tenthTagCount)) {
                    tags.add(tag);
                    recipeCountByTag.add(recipeCount);
                } else {
                    break;
                }
            }
        }
        response.put("tags", tags);
        response.put("recipeCountByTag", recipeCountByTag);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMemberList")
    public ResponseEntity<List<Member>> showMemberList() {
        List<Member> members = userService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @PostMapping("/getMemberList/searchById")
    public ResponseEntity<?> getMemberById(@RequestParam("query") String query) {
        List<Member> members = new ArrayList<>();
        if (query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty.");
        }
        try {
            int id = Integer.parseInt(query);
            Member member = userService.getMemberById(id);
            if (member != null) {
                members.add(member);
            } else {
                return ResponseEntity.status(404).body("Member not found.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error searching: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred during the search.");
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/getReportsByIdOfMemberReported/{id}")
    public ResponseEntity<?> showMemberReports(@PathVariable("id") Integer memberId) {
        Member member = userService.getMemberById(memberId);
        if (member == null) {
            return ResponseEntity.status(404).body("Member not found.");
        }
        List<MemberReport> memberReports = reportService.getApprovedReportsByMemberReported(member.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("member", member);
        response.put("memberReports", memberReports);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteMemberById/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable("id") Integer memberId) {
        try {
            ResponseEntity<String> userServiceResponse = userService.deleteMember(memberId);
            if (userServiceResponse.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<String> recipeServiceResponse = recipeService.deleteRecipesByMemberId(memberId);
                if (recipeServiceResponse.getStatusCode() == HttpStatus.OK) {
                    return ResponseEntity.ok("Member and all recipes deleted successfully.");
                } else {
                    return ResponseEntity.status(recipeServiceResponse.getStatusCode())
                            .body("Failed to delete member's recipes.");
                }
            }
            else {
                return ResponseEntity.status(userServiceResponse.getStatusCode()).body("Failed to delete member: " + userServiceResponse.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/getAllPendingRecipeReports")
    public ResponseEntity<List<RecipeReport>> showPendingRecipeReports() {
        List<RecipeReport> pendingReports = reportService.getAllPendingRecipeReports();
        return ResponseEntity.ok(pendingReports);
    }

    @GetMapping("/getAllPendingMemberReports")
    public ResponseEntity<List<MemberReport>> showPendingMemberReports() {
        List<MemberReport> pendingReports = reportService.getAllPendingMemberReports();
        return ResponseEntity.ok(pendingReports);
    }

    @GetMapping("/getRecipeReport/{id}")
    public ResponseEntity<?> showRecipeReportDetails(@PathVariable(value = "id") Integer id) {
        RecipeReport report = reportService.getRecipeReportById(id);
        Integer recipeReportedId = reportService.getRecipeIdByRecipeReportId(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("report", report);
        response.put("recipe", recipeService.getRecipeById(recipeReportedId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMemberReport/{id}")
    public ResponseEntity<?> showMemberReportDetails(@PathVariable(value = "id") Integer id) {
        MemberReport report = reportService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/approveRecipeReport/{id}")
    public ResponseEntity<String> approveRecipeReport(@PathVariable(value = "id") Integer id) {
        RecipeReport report = reportService.getRecipeReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        adminService.approveRecipeReport(id);
        return ResponseEntity.ok("Recipe report approved successfully.");
    }

    @PostMapping("/rejectRecipeReport/{id}")
    public ResponseEntity<String> rejectRecipeReport(@PathVariable(value = "id") Integer id) {
        RecipeReport report = reportService.getRecipeReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        adminService.rejectRecipeReport(id);
        return ResponseEntity.ok("Recipe report rejected successfully.");
    }

    @PostMapping("/approveMemberReport/{id}")
    public ResponseEntity<String> approveMemberReport(@PathVariable(value = "id") Integer id) {
        MemberReport report = reportService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        adminService.approveMemberReport(id);
        return ResponseEntity.ok("Member report approved successfully.");
    }

    @PostMapping("/rejectMemberReport/{id}")
    public ResponseEntity<String> rejectMemberReport(@PathVariable(value = "id") Integer id) {
        MemberReport report = reportService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        adminService.rejectMemberReport(id);
        return ResponseEntity.ok("Member report rejected successfully.");
    }

}
