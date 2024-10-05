package nus.iss.se.team9.admin_service_team9;
import nus.iss.se.team9.admin_service_team9.service.*;
import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
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
            Long recipeCount = (Long) tagCount[1];

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

    @GetMapping("/memberManage")
    public ResponseEntity<List<Member>> showMemberList() {
        List<Member> members = adminService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @PostMapping("/memberManage/search")
    public ResponseEntity<?> showMemberById(@RequestParam("query") String query) {
        List<Member> members = new ArrayList<>();
        if (query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty.");
        }
        try {
            int id = Integer.parseInt(query);
            Member member = adminService.getMemberById(id);
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

    @GetMapping("/memberManage/{id}/reports")
    public ResponseEntity<?> showMemberReports(@PathVariable("id") Integer memberId) {
        Member member = adminService.getMemberById(memberId);
        if (member == null) {
            return ResponseEntity.status(404).body("Member not found.");
        }
        List<MemberReport> memberReports = adminService.getReportsByMember(member);
        Map<String, Object> response = new HashMap<>();
        response.put("member", member);
        response.put("memberReports", memberReports);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/memberManage/delete/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable("id") Integer memberId) {
        Member member = adminService.getMemberById(memberId);
        if (member == null) {
            return ResponseEntity.status(404).body("Member not found.");
        }
        adminService.deleteMember(memberId);
        return ResponseEntity.ok("Member deleted successfully.");
    }

    @GetMapping("/recipeReport")
    public ResponseEntity<List<RecipeReport>> showPendingRecipeReports() {
        List<RecipeReport> pendingReports = adminService.getPendingRecipeReport();
        return ResponseEntity.ok(pendingReports);
    }

    @GetMapping("/recipeReport/{id}")
    public ResponseEntity<?> showRecipeReportDetails(@PathVariable(value = "id") Integer id) {
        RecipeReport report = adminService.getRecipeReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("report", report);
        response.put("recipe", report.getRecipeReported());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recipeReport/{id}/approve")
    public ResponseEntity<String> approveRecipeReport(@PathVariable(value = "id") Integer id) {
        RecipeReport report = adminService.getRecipeReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        adminService.approveRecipeReport(id);
        return ResponseEntity.ok("Recipe report approved successfully.");
    }

    @PostMapping("/recipeReport/{id}/reject")
    public ResponseEntity<String> rejectRecipeReport(@PathVariable(value = "id") Integer id) {
        // 检查报告是否存在
        RecipeReport report = adminService.getRecipeReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Recipe report not found.");
        }
        adminService.rejectRecipeReport(id);
        return ResponseEntity.ok("Recipe report rejected successfully.");
    }

    // show all reported members
    @GetMapping("/memberReport")
    public ResponseEntity<List<MemberReport>> showPendingMemberReports() {
        List<MemberReport> pendingReports = adminService.getPendingMemberReport();
        return ResponseEntity.ok(pendingReports);
    }

    @GetMapping("/memberReport/{id}")
    public ResponseEntity<?> showMemberReportDetails(@PathVariable(value = "id") Integer id) {
        MemberReport report = adminService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/memberReport/{id}/approve")
    public ResponseEntity<String> approveMemberReport(@PathVariable(value = "id") Integer id) {
        MemberReport report = adminService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        adminService.approveMemberReport(id);
        return ResponseEntity.ok("Member report approved successfully.");
    }


    @PostMapping("/memberReport/{id}/reject")
    public ResponseEntity<String> rejectMemberReport(@PathVariable(value = "id") Integer id) {
        MemberReport report = adminService.getMemberReportById(id);
        if (report == null) {
            return ResponseEntity.status(404).body("Member report not found.");
        }
        adminService.rejectMemberReport(id);
        return ResponseEntity.ok("Member report rejected successfully.");
    }


}
