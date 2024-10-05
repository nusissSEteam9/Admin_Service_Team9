package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RecipeService {

    @Autowired
    RecipeRepository recipeRepo;
    public List<Recipe> getAllRecipesByYear(int year) {
        return recipeRepo.getAllRecipesByYear(year);
    }

    public List<Object[]> getRecipeCountByTag() {
        return recipeRepo.getRecipeCountByTag();
    }
}
