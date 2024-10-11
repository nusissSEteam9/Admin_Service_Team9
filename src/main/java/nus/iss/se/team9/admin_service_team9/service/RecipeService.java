package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Transactional
public class RecipeService {
    private final RestTemplate restTemplate;
    private final String recipeServiceUrl;
    @Autowired
    public RecipeService(RestTemplate restTemplate, @Value("${recipe.service.url}") String recipeServiceUrl) {
        this.restTemplate = restTemplate;
        this.recipeServiceUrl = recipeServiceUrl;
    }

    public List<Recipe> getAllRecipesByYear(int year) {
        String url = recipeServiceUrl +"/count-by-year/" + year;
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public List<Object[]> getRecipeCountByTag() {
        String url = recipeServiceUrl + "/count-by-tag";
        System.out.println("go to recipe-api");
        ResponseEntity<List<Object[]>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public List<Recipe> getRecipesByOrder(String orderBy, String order) {
        String url = recipeServiceUrl + String.format("?orderBy=%s&order=%s", orderBy, order);
        System.out.println("go to recipe-api");
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public ResponseEntity<String> deleteRecipesByMemberId(Integer memberId) {
        String url = recipeServiceUrl + "/delete-by-member";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Integer> requestEntity = new HttpEntity<>(memberId, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Recipes deleted successfully");
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Failed to delete recipes");
        }
    }

    public void deleteRecipe(Integer id) {
        String url = recipeServiceUrl + "/" + id;
        ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.DELETE, null, Void.class);
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            System.out.println("Recipe deleted successfully");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            System.out.println("Recipe not found");
        } else {
            System.out.println("Error occurred: " + response.getStatusCode());
        }
    }

    public Recipe getRecipeById(Integer id) {
        String url = recipeServiceUrl + "/" + id;
        try {
            ResponseEntity<Recipe> response = restTemplate.getForEntity(url, Recipe.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Recipe not found or deleted, status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Recipe with ID " + id + " not found.");
            } else {
                throw new RuntimeException("Error while fetching recipe: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

}
