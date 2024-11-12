package nus.iss.se.team9.admin_service_team9;

import nus.iss.se.team9.admin_service_team9.contoller.AdminController;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.service.AdminService;
import nus.iss.se.team9.admin_service_team9.service.RecipeService;
import nus.iss.se.team9.admin_service_team9.service.ReportService;
import nus.iss.se.team9.admin_service_team9.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ReportService reportService;

    private Member member;
    private Recipe recipe;
    private RecipeReport recipeReport;
    private RecipeReport recipeReport2;
    private MemberReport memberReport;

    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setId(1);
        member.setUsername("TestUser");

        recipe = new Recipe();
        recipe.setId(1);
        recipe.setName("Test Recipe");

        recipeReport = new RecipeReport();
        recipeReport.setId(1);
        recipeReport.setRecipeReported(recipe);
        recipeReport.setMember(member);
        recipeReport.setStatus(Status.PENDING);
        recipeReport.setReason("Test Reason");

        recipeReport2 = new RecipeReport();
        recipeReport2.setId(2);
        recipeReport2.setRecipeReported(recipe);
        recipeReport2.setMember(member);
        recipeReport2.setStatus(Status.PENDING);
        recipeReport2.setReason("Test Reason2");

        memberReport = new MemberReport();
        memberReport.setId(1);
        memberReport.setMemberReported(member);
        memberReport.setMember(member);
        memberReport.setStatus(Status.PENDING);
        memberReport.setReason("Test Reason");
    }


    @Test
    void testCheckHealth() throws Exception {
        mockMvc.perform(get("/admin/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is connected"));
    }


    @Test
    void testGetDashboard() throws Exception {
        List<Recipe> recipesByYear = new ArrayList<>(); // Add sample data if needed
        List<Object[]> tagCounts = Arrays.asList(
                new Object[]{"Dessert", 5L},
                new Object[]{"Main Course", 3L}
        );

        when(recipeService.getAllRecipesByYear(2003)).thenReturn(recipesByYear);
        when(recipeService.getRecipeCountByTag()).thenReturn(tagCounts);

        mockMvc.perform(get("/admin/getDashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.months").isArray())
                .andExpect(jsonPath("$.recipeCountByMonth").isArray())
                .andExpect(jsonPath("$.tags[0]").value("Dessert"))
                .andExpect(jsonPath("$.recipeCountByTag[0]").value(5));
    }





    @Test
    void testDeleteMember() throws Exception {
        when(userService.deleteMember(1)).thenReturn(ResponseEntity.ok("Member deleted successfully"));
        when(recipeService.deleteRecipesByMemberId(1)).thenReturn(ResponseEntity.ok("Recipes deleted successfully"));

        mockMvc.perform(delete("/admin/deleteMemberById/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Member and all recipes deleted successfully."));
    }

    @Test
    void testShowPendingRecipeReports() throws Exception {

        List<RecipeReport> pendingReports = Arrays.asList(recipeReport,recipeReport2);

        when(reportService.getAllPendingRecipeReports()).thenReturn(pendingReports);

        mockMvc.perform(get("/admin/getAllPendingRecipeReports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testApproveRecipeReport() throws Exception {
        RecipeReport report = recipeReport;

        when(reportService.getRecipeReportById(1)).thenReturn(report);

        mockMvc.perform(post("/admin/approveRecipeReport/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe report approved successfully."));
    }

    @Test
    void testRejectRecipeReport() throws Exception {
        RecipeReport report = recipeReport;

        when(reportService.getRecipeReportById(1)).thenReturn(report);

        mockMvc.perform(post("/admin/rejectRecipeReport/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe report rejected successfully."));
    }


}
