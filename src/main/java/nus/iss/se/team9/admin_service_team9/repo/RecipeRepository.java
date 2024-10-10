package nus.iss.se.team9.admin_service_team9.repo;

import nus.iss.se.team9.admin_service_team9.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

	@Query("SELECT r FROM Recipe r WHERE FUNCTION('YEAR', r.submittedDate) = :year ORDER BY r.submittedDate")
	List<Recipe> getAllRecipesByYear(@Param("year") int year);

	@Query("SELECT t, COUNT(r) AS recipeCount FROM Recipe r JOIN r.tags t GROUP BY t ORDER BY recipeCount DESC")
	List<Object[]> getRecipeCountByTag();

	List<Recipe> findAllByOrderByRatingAsc();

	List<Recipe> findAllByOrderByRatingDesc();

	List<Recipe> findAllByOrderByNumberOfSavedAsc();

	List<Recipe> findAllByOrderByNumberOfSavedDesc();

	List<Recipe> findAllByOrderByHealthScoreAsc();

	List<Recipe> findAllByOrderByHealthScoreDesc();

}
