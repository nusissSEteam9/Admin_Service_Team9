package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import nus.iss.se.team9.admin_service_team9.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RecipeService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${recipe.service.url}")
    private String recipeServiceUrl;

    public List<Recipe> getAllRecipesByYear(int year) {
        String url = recipeServiceUrl +"/count-by-year/" + year;
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recipe>>() {});
        return response.getBody();
    }

    public List<Object[]> getRecipeCountByTag() {
        String url = recipeServiceUrl + "/count-by-tag";
        System.out.println("go to recipe-api");
        ResponseEntity<List<Object[]>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Object[]>>() {});
        return response.getBody();
    }

    public List<Recipe> getRecipesByOrder(String orderBy, String order) {
        String url = recipeServiceUrl + String.format("?orderBy=%s&order=%s", orderBy, order);
        System.out.println("go to recipe-api");
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recipe>>() {});
        return response.getBody();
    }
    public void deleteMemberRecipes(Member member) {
        String url = recipeServiceUrl + "/delete-by-member";
        HttpEntity<Member> request = new HttpEntity<>(member);
        restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
    }

    public void deleteRecipe(Integer id){
        String url = recipeServiceUrl +"/set-recipe-to-deleted/" + id;
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.DELETE, null, new ParameterizedTypeReference<List<Recipe>>() {});
    }
}
