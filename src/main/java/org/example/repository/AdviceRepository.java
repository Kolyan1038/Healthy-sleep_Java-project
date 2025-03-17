package org.example.repository;

import java.util.List;
import org.example.model.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {
    
    @Query("SELECT DISTINCT sa FROM Advice sa "
            + "LEFT JOIN FETCH sa.users")
    List<Advice> findAllWithUsers();
    
    @Query("SELECT sa FROM Advice sa "
            + "WHERE sa.recommendedHours = :recommendedHours")
    List<Advice> findByRecommendedHours(@Param("recommendedHours") int recommendedHours);
    
    @Query("SELECT sa FROM Advice sa "
            + "WHERE sa.recommendedHours > :minHours OR sa.recommendedHours < :maxHours")
    List<Advice> findByRecommendedHoursRange(@Param("minHours") int minHours,
                                                  @Param("maxHours") int maxHours);
    
    @Query("SELECT sa FROM Advice sa "
            + "JOIN sa.users u "
            + "WHERE u.id = :userId")
    List<Advice> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT sa FROM Advice sa "
            + "LEFT JOIN sa.users u "
            + "WHERE u IS NULL")
    List<Advice> findUnassignedAdvices();
    
    @Query("SELECT sa FROM Advice sa "
            + "LEFT JOIN sa.users u "
            + "GROUP BY sa.id "
            + "ORDER BY COUNT(u) DESC")
    List<Advice> findAllOrderByUserCountDesc();
    
    @Query("SELECT sa FROM Advice sa "
            + "WHERE sa.recommendedHours > :hours")
    List<Advice> findByRecommendedHoursGreaterThan(@Param("hours") int hours);
    
    @Query("SELECT sa FROM Advice sa "
            + "WHERE sa.recommendedHours < :hours")
    List<Advice> findByRecommendedHoursLessThan(@Param("hours") int hours);
}