package nus.iss.se.team9.admin_service_team9.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public ResponseEntity<String> deleteRecipesByMemberId(Integer memberId) {
        String url = recipeServiceUrl + "/delete-by-member";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Integer> requestEntity = new HttpEntity<>(memberId, headers);

        // 使用 RestTemplate 调用 DELETE 请求
        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        // 检查响应的状态码并返回适当的 ResponseEntity
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Recipes deleted successfully");
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Failed to delete recipes");
        }
    }


    public void deleteRecipe(Integer id){
        String url = recipeServiceUrl +"/set-recipe-to-deleted/" + id;
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                url, HttpMethod.DELETE, null, new ParameterizedTypeReference<List<Recipe>>() {});
    }
}
