package nus.iss.se.team9.admin_service_team9.contoller;

import jakarta.servlet.http.HttpServletResponse;

import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.service.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/csv")
public class CsvController {

    private final RecipeService recipeService;
    private final CsvService csvService;

    @Autowired
    public CsvController(RecipeService recipeService, CsvService csvService) {
        this.recipeService = recipeService;
        this.csvService = csvService;
    }

    @GetMapping("/download/{orderBy}/{order}")
    public void downloadCsv(HttpServletResponse response, @PathVariable String orderBy, @PathVariable String order) {
        try {
            String fileName = "csvReportForAdmin";
            List<Recipe> dataList = recipeService.getRecipesByOrder(orderBy, order);
            csvService.generateCsv(dataList, fileName);
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
            FileInputStream fileInputStream = new FileInputStream(fileName + ".csv");
            IOUtils.copy(fileInputStream, response.getOutputStream());
            response.flushBuffer();
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            // 处理异常
        }
    }
}