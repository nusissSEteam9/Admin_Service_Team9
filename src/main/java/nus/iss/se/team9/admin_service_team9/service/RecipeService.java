package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Recipe> getRecipesByOrder(String orderBy, String order) {
        List<Recipe> recipes = new ArrayList<>();
        if (orderBy.equals("rating")) {
            if (order.equals("asc")) {
                recipes = recipeRepo.findAllByOrderByRatingAsc();
            } else if (order.equals("desc")) {
                recipes = recipeRepo.findAllByOrderByRatingDesc();
            }
        } else if (orderBy.equals("numberOfSaved")) {
            if (order.equals("asc")) {
                recipes = recipeRepo.findAllByOrderByNumberOfSavedAsc();
            } else if (order.equals("desc")) {
                recipes = recipeRepo.findAllByOrderByNumberOfSavedDesc();
            }
        } else if (orderBy.equals("healthScore")) {
            if (order.equals("asc")) {
                recipes = recipeRepo.findAllByOrderByHealthScoreAsc();
            } else if (order.equals("desc")) {
                recipes = recipeRepo.findAllByOrderByHealthScoreDesc();
            }
        }
        else {
            recipes = recipeRepo.findAll();
        }
        return recipes;
    }
}
